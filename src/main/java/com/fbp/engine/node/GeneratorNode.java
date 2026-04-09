package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import java.util.HashMap;
import java.util.Map;

public class GeneratorNode extends AbstractNode {
    private final int maxCount; // 생성할 메시지 총 개수

    public GeneratorNode(String id, int maxCount) {
        super(id);
        addOutputPort("out");
        this.maxCount = maxCount;
    }

    @Override
    public void run() {
        try {
            // 외부에서 interrupt를 걸거나 정해진 개수를 다 채울 때까지 반복
            for (int i = 0; i < maxCount && !Thread.currentThread().isInterrupted(); i++) {
                Map<String, Object> data = new HashMap<>();
                data.put("count", i);
                data.put("value", "data-" + i);

                send("out", new Message(data));

                // 너무 빨리 생성하면 다른 노드가 처리하기 힘들 수 있으니 아주 잠깐 쉽니다.
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdown(); // 작업 완료 후 노드 종료 로그 출력
        }
    }

    @Override
    protected void onProcess(Message message) {
        // 입력 포트가 없으므로 사용하지 않음
    }
}