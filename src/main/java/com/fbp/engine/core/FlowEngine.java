package com.fbp.engine.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowEngine {
    @Getter
    private final Map<String, Flow> flows;
    @Getter
    private State state;
    public enum State {
        INITIALIZED, RUNNING, STOPPED
    }

    public FlowEngine(){
        flows = new HashMap<>();
        state = State.INITIALIZED;
    }

    public void register(Flow flow){
        flows.put(flow.getId(), flow);
        System.out.printf("[Engine] 플로우 '%s' 등록됨%n", flow.getId());
    }

    public void startFlow(String flowId){
        Flow flow = flows.get(flowId);
        if(flow == null){
            throw new IllegalArgumentException("flow 없음");
        }
        List<String> errors = flow.validate();
        if(!errors.isEmpty()){
            throw new IllegalStateException("에러가 존재");
        }
        flow.initialize();
        state = State.RUNNING;
        System.out.printf("[Engine] 플로우 '%s' 시작됨%n", flow.getId());
    }

    public void stopFlow(String flowId){
        Flow flow = flows.get(flowId);
        if(flow == null){
            throw new IllegalArgumentException("flow 없음");
        }
        flow.shutdown();
        System.out.printf("[Engine] 플로우 '%s' 정지됨%n", flow.getId());
    }

    public void shutdown(){
        for(Flow f : flows.values()){
            f.shutdown();
        }
        state = State.STOPPED;
    }

    public void listFlows(){
        for(Flow f : flows.values()){
            System.out.printf("플로우 ID : %s, 상태 : %s%n",f.getId(), this.state);
        }
    }
}
