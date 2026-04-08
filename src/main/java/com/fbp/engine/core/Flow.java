package com.fbp.engine.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flow {
    private String id;
    @Getter
    private Map<String, AbstractNode> nodes = new HashMap<>();
    @Getter
    private List<Connection> connections = new ArrayList<>();
    private final List<Thread> runningThreads = new ArrayList<>();
    private enum State { UNVISITED, VISITED, VISITING}

    public Flow addNode(AbstractNode node){
        nodes.put(node.getId(), node);
        return this;
    }

    public Flow connect(String sourceNodeId, String sourcePort, String targetNodeId, String targetPort) {
        AbstractNode source = nodes.get(sourceNodeId);
        AbstractNode target = nodes.get(targetNodeId);

        if (source == null) throw new IllegalArgumentException("소스 노드 없음: " + sourceNodeId);
        if (target == null) throw new IllegalArgumentException("대상 노드 없음: " + targetNodeId);

        if (source.getOutputPort(sourcePort) == null)
            throw new IllegalArgumentException("소스 포트 없음: " + sourcePort);
        if (target.getInputPort(targetPort) == null)
            throw new IllegalArgumentException("대상 포트 없음: " + targetPort);

        Connection connection = new Connection();
        source.getOutputPort(sourcePort).connect(connection);
        connection.setTarget(target.getInputPort(targetPort));
        ((DefaultInputPort)target.getInputPort(targetPort)).setConnection(connection);

        connections.add(connection);
        return this;
    }

    public void initialize(){
        for(AbstractNode a : nodes.values()){
            a.initialize();

            Thread thread = new Thread(a, a.getId());
            thread.setDaemon(true);
            runningThreads.add(thread);
            thread.start();
        }
    }

    public void shutdown(){
        for(Thread t : runningThreads){
            t.interrupt();
        }
        for(AbstractNode a : nodes.values()){
            a.shutdown();
        }
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (nodes.isEmpty()) {
            errors.add("에러: 등록된 노드가 0개입니다.");
        }

        if (!nodes.isEmpty()) {
            Map<String, State> stateMap = new HashMap<>();
            for (String nodeId : nodes.keySet()) {
                stateMap.put(nodeId, State.UNVISITED);
            }

            for (String nodeId : nodes.keySet()) {
                if (stateMap.get(nodeId) == State.UNVISITED) {
                    if (hasCycle(nodeId, stateMap, errors)) {
                        // 순환이 발견되면 에러 메시지가 hasCycle 내부에서 추가됨
                    }
                }
            }
        }

        return errors;
    }

    private boolean hasCycle(String currentId, Map<String, State> stateMap, List<String> errors) {
        stateMap.put(currentId, State.VISITING);

        AbstractNode currentNode = nodes.get(currentId);

        List<String> neighbors = getNextNodeIds(currentNode);

        for (String nextId : neighbors) {
            State nextState = stateMap.get(nextId);

            if (nextState == State.VISITING) {
                errors.add("에러: 순환 참조가 탐지되었습니다. (" + currentId + " -> " + nextId + ")");
                return true;
            }

            if (nextState == State.UNVISITED) {
                if (hasCycle(nextId, stateMap, errors)) return true;
            }
        }

        stateMap.put(currentId, State.VISITED);
        return false;
    }

    private List<String> getNextNodeIds(AbstractNode node) {
        List<String> nextIds = new ArrayList<>();

        for (OutputPort outPort : node.getOutputPorts().values()) {
            DefaultOutputPort dop = (DefaultOutputPort) outPort;

            for (Connection conn : dop.getConnections()) {
                InputPort targetPort = conn.getTarget();

                if (targetPort instanceof DefaultInputPort) {
                    Node targetNode = ((DefaultInputPort) targetPort).getOwner();
                    nextIds.add(targetNode.getId());
                }
            }
        }
        return nextIds;
    }
}
