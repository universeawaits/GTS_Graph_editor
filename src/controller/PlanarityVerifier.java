package controller;

import model.*;

import java.util.*;


public class PlanarityVerifier {
    private Graph graph;
    private AdjacencyMatrix adjacencyMatrix;


    public PlanarityVerifier(Graph graph, AdjacencyMatrix adjacencyMatrix) {
        this.graph = graph;
        this.adjacencyMatrix = adjacencyMatrix;
    }

    private boolean dfsCycle(List<Node> trackingCycle, Map<Node, Integer> visitedNodes, Node parentNode, Node currentNode) {
        visitedNodes.replace(currentNode, 1);
        for (Node node : graph.getNodes()) {
            if (node.equals(parentNode)) {
                continue;
            }

            if (!adjacencyMatrix.adjacentNodesOf(currentNode).contains(node)) {
                continue;
            }

            if (visitedNodes.get(node) == 0) {
                trackingCycle.add(currentNode);
                if (dfsCycle(trackingCycle, visitedNodes, currentNode, node)) {
                    return true;
                } else {
                    trackingCycle.remove(trackingCycle.size() - 1);
                }
            }

            if (visitedNodes.get(node) == 1) {
                trackingCycle.add(currentNode);
                //Найден цикл
                List<Node> cycle = new ArrayList<>();
                //"Выдергиваем" вершины цикла из порядка обхода
                for (int cycleNodeIter = 0; cycleNodeIter < cycle.size(); cycleNodeIter++) {
                    if (trackingCycle.get(cycleNodeIter).equals(node)) {
                        cycle.addAll(trackingCycle.subList(cycleNodeIter, trackingCycle.size()));
                        trackingCycle.clear();
                        trackingCycle.addAll(cycle);

                        return true;
                    }
                }

                return true;
            }
        }

        visitedNodes.replace(currentNode, 2);

        return false;
    }

    public List<Node> getCycle() {
        List<Node> cycle = new ArrayList<>();
        Map<Node, Integer> visitedNodes = new HashMap<>();
        for (Node node : graph.getNodes()) {
            visitedNodes.put(node, 0);
        }

        boolean hasCycle = dfsCycle(cycle, visitedNodes, new Node(), graph.getNodes().get(0));

        if (!hasCycle) {
            return null;
        }
        else {
            List<Node> resultCycle = new ArrayList<>();

            for (Node node : cycle) {
                resultCycle.add(node);
            }

            return resultCycle;
        }
    }

    //Поиск связных компонент графа G - G', дополненного ребрами из G,
    // один из концов которых принадлежит связной компоненте, а другой G'
    private void dfsSegments(Map<Node, Boolean> visitedNodes, Map<Node, Boolean> laidNodes, Graph result, Node currentNode) {
        visitedNodes.replace(currentNode, true);
        for (Node node : graph.getNodes()) {
            if (adjacencyMatrix.adjacentNodesOf(currentNode).contains(node) || adjacencyMatrix.adjacentNodesOf(node).contains(currentNode)) {
                result.getArcs().add(new Arc(currentNode, node));
                if (!visitedNodes.get(node) && !laidNodes.get(node)) {
                    dfsSegments(visitedNodes, laidNodes, result, currentNode);
                }
            }
        }
    }

    private List<Graph> getSegments(Map<Node, Boolean> laidNodes, Map<Arc, Boolean> laidArcs) {
        List<Graph> segments = new ArrayList<>();

        //Поиск однореберных сегментов
        for (int sourceGraphNodeIter = 0; sourceGraphNodeIter < graph.getNodes().size(); sourceGraphNodeIter++) {
            for (int j = sourceGraphNodeIter + 1; j < graph.getNodes().size(); j++) {
                if (adjacencyMatrix.adjacentNodesOf(
                                graph.getNodes().get(sourceGraphNodeIter)).contains(graph.getNodes().get(j))
                        && !laidArcs.keySet().contains(
                                new Arc(graph.getNodes().get(sourceGraphNodeIter), graph.getNodes().get(j)))
                        && laidNodes.get(graph.getNodes().get(sourceGraphNodeIter))
                        && laidNodes.get(graph.getNodes().get(j))) {

                    Graph t = new Graph();
                    t.getArcs().add(new Arc(graph.getNodes().get(sourceGraphNodeIter), graph.getNodes().get(j)));

                    segments.add(t);
                }
            }
        }

        //Поиск связных компонент графа G - G', дополненного ребрами из G,
        // один из концов которых принадлежит связной компоненте, а другой G'
        Map<Node, Boolean> visitedNodes = new HashMap<>();

        for (Node node : graph.getNodes()) {
            visitedNodes.put(node, false);
        }

        for (Node node : graph.getNodes()) {
            if (!visitedNodes.get(node) && !laidNodes.get(node)) {
                Graph result = new Graph();
                dfsSegments(visitedNodes, laidNodes, result, node);
                segments.add(result);
            }
        }

        return segments;
    }

    //Поиск цепи в выбранном сегменте, используя DFS алгоритм
    private void dfsPath(Map<Node, Boolean> visitedNodes, Map<Node, Boolean> laidNodes, List<Node> path, Node currentNode) {
        visitedNodes.replace(currentNode, true);
        path.add(currentNode);

        for (Node node : graph.getNodes()) {
            if (adjacencyMatrix.adjacentNodesOf(currentNode).contains(node) && !visitedNodes.get(node)) {
                if (!laidNodes.get(node)) {
                    dfsPath(visitedNodes, laidNodes, path, currentNode);
                } else {
                    path.add(node);
                }

                return;
            }
        }
    }

    private List<Node> getChain(Map<Node, Boolean> laidNodes, Graph segment) {
        List<Node> resultChain = new ArrayList<>();

        for (Node begin : segment.getNodes()) {
            if (laidNodes.get(begin)) {
                boolean inGraph = false;

                for (Node end : segment.getNodes()) {
                    if (segment.getArcs().contains(new Arc(begin, end))) {
                        inGraph = true;
                    }
                }

                if (inGraph) {
                    Map<Node, Boolean> visitedNodes = new HashMap<>();

                    for (Node node : segment.getNodes()) {
                        visitedNodes.put(node, false);
                    }

                    dfsPath(visitedNodes, laidNodes, resultChain, begin);

                    break;
                }
            }
        }

        return resultChain;
    }

    //Укладка цепи, описание матрицы смежности
    public static void layPath(Map<Arc, Boolean> laidPath, List<Node> path, boolean isCyclic) {
        for (int pathNodeIter = 0; pathNodeIter < path.size() - 1; pathNodeIter++) {
            laidPath.replace(new Arc(path.get(pathNodeIter), path.get(pathNodeIter + 1)), true);
            laidPath.replace(new Arc(path.get(pathNodeIter + 1), path.get(pathNodeIter)), true);
        }
        if (isCyclic) {
            laidPath.replace(new Arc(path.get(0), path.get(path.size() - 1)), true);
            laidPath.replace(new Arc(path.get(path.size() - 1), path.get(0)), true);
        }
    }

    //Проверка на то, что данный сегмент содержится в данной грани
    private boolean isFaceContainsSegment(final List<Node> face, final Graph segment, Map<Node, Boolean> laidNodes) {
        for (Node begin : graph.getNodes()) {
            for (Node end : graph.getNodes()) {
                if (segment.getArcs().contains(new Arc(begin, end))) {
                    if ((laidNodes.get(begin) && !face.contains(begin)) || (laidNodes.get(end) && !face.contains(end))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //Считаем число граней, вмещающих данные сегменты
    private List<Integer> numberOfFacesContainedSegments(List<List<Node>> interFaces, List<Node> exterFace,
                                                         List<Graph> segments, Map<Node, Boolean> laidNodes,
                                                         List<List<Node>> destFaces) {

        List<Integer> count = new ArrayList<>(Collections.nCopies(segments.size(), 0));

        for (int i = 0; i < segments.size(); i++) {
            for (List<Node> face : interFaces) {
                if (isFaceContainsSegment(face, segments.get(i), laidNodes)) {
                    destFaces.set(i, face);
                    count.set(i, count.get(i) + 1);
                }
            }

            if (isFaceContainsSegment(exterFace, segments.get(i), laidNodes)) {
                destFaces.set(i, exterFace);
                count.set(i, count.get(i) + 1);
            }
        }

        return count;
    }

    //Получить плоскую укладку графа
    //Возвращаются все грани уложенного планарного графа
    //Если это невозможно (граф не планарный), то null
    public Faces getPlanarLaying() {
        //Если граф одновершинный, то возвращаем две грани
        if (graph.getNodes().size() == 1) {
            List<List<Node>> faces = new ArrayList<>();
            List<Node> outerFace = new ArrayList<>();
            outerFace.add(graph.getNodes().get(0));
            faces.add(outerFace);
            faces.add(outerFace);
            return new Faces(faces, outerFace);
        }

        //Ищем цикл, если его нет, до граф не соответствует условиям алгоритма
        //(Нет циклов => дерево => планарный)
        List<Node> cycle = getCycle();

        if ((cycle == null) || cycle.isEmpty()) {
            return null;
        }

        //Списки граней
        List<List<Node>> intFaces = new ArrayList<>();
        List<Node> extFace = new ArrayList<>(cycle);
        intFaces.add(cycle);
        intFaces.add(extFace);

        //Массивы уже уложенных вершин и ребер соответственно
        Map<Node, Boolean> laidNodes = new HashMap<>();
        Map<Arc, Boolean> laidArcs = new HashMap<>();

        for (Node node : graph.getNodes()) {
            laidNodes.put(node, false);
        }

        for (Arc arc : graph.getArcs()) {
            laidArcs.put(arc, false);
        }

        for (Node node : graph.getNodes()) {
            laidNodes.put(node, false);
        }

        for (Node node : cycle) {
            laidNodes.replace(node, true);
        }

        //Укладываем найденный цикл
        layPath(laidArcs, cycle, true);

        //Второй шаг алгоритма:
        //выделение множества сегментов, подсчет числа вмещающих граней,
        //выделение цепей из сегментов, укладка цепей, добавление новых граней
        while (true) {
            List<Graph> segments = getSegments(laidNodes, laidArcs);

            //Если нет сегментов, го граф - найденный постой цикл => планарный
            if (segments.size() == 0) {
                break;
            }

            //Массив граней, в которые будут уложены соответствующие сегменты с минимальным числом numberOfFacesContainedSegments()
            List<List<Node>> destFaces = new ArrayList<>(Collections.nCopies(segments.size(), new ArrayList<>()));

            List<Integer> count = numberOfFacesContainedSegments(intFaces, extFace, segments,laidNodes, destFaces);

            //Ищем минимальное число numberOfFacesContainedSegments()
            int minCount = 0;

            for (int i = 0; i < segments.size(); i++) {
                if (count.get(i) < count.get(minCount)) {
                    minCount = i;
                }
            }

            //Если хотя бы одно ноль, то граф не планарный
            if (count.get(minCount) == 0) {
                return null;
            } else {
                //Укладка выбранного сегмента
                //Выделяем цепь между двумя контактными вершинами
                List<Node> path = getChain(laidNodes, segments.get(minCount));

                //Помечаем вершины цепи как уложенные
                for (Node node : path) {
                    laidNodes.replace(node, true);
                }
                //Укладываем соответствующие ребра цепи
                layPath(laidArcs, path, false);

                //Целевая грань, куда будет уложен выбранный сегмент
                List<Node> face = destFaces.get(minCount);

                //Новые грани, порожденные разбиением грани face выбранным сегментом
                List<Node> face1 = new ArrayList<>();
                List<Node> face2 = new ArrayList<>();

                //Ищем номера контактных вершин цепи
                int contactFirst = 0, contactSecond = 0;

                for (int i = 0; i < face.size(); i++) {
                    if (face.get(i).equals(path.get(0))) {
                        contactFirst = i;
                    }
                    if (face.get(i).equals(path.get(path.size() - 1))) {
                        contactSecond = i;
                    }
                }

                //Находим обратную цепь (цепь, пробегаемая в обратном направлении)
                List<Node> reversedPath = new ArrayList<>(path);
                Collections.reverse(reversedPath);

                int faceSize = face.size();

                if (face != extFace) {
                    //Если целевая грань не внешняя
                    //Укладываем прямую цепь в одну из порожденных граней,
                    //а обратную в другую в зависимости от номеров контактных вершин
                    if (contactFirst < contactSecond) {
                        face1.addAll(path);
                        for (int i = (contactSecond + 1) % faceSize; i != contactFirst; i = (i + 1) % faceSize) {
                            face1.add(face.get(i));
                        }
                        face2.addAll(reversedPath);
                        for (int i = (contactFirst + 1) % faceSize; i != contactSecond; i = (i + 1) % faceSize) {
                            face2.add(face.get(i));
                        }
                    } else {
                        face1.addAll(reversedPath);
                        for (int i = (contactFirst + 1) % faceSize; i != contactSecond; i = (i + 1) % faceSize) {
                            face1.add(face.get(i));
                        }
                        face2.addAll(path);
                        for (int i = (contactSecond + 1) % faceSize; i != contactFirst; i = (i + 1) % faceSize) {
                            face2.add(face.get(i));
                        }
                    }

                    //Удаляем целевую грань(она разбилась на две новые)
                    //Добавляем порожденные грани в множество внутренних граней
                    intFaces.remove(face);
                    intFaces.add(face1);
                    intFaces.add(face2);
                } else {
                    //Если целевая грань совпала с внешней
                    //Все то же самое, только одна из порожденных граней - новая внешняя грань
                    List<Node> newOuterFace = new ArrayList<>();
                    if (contactFirst < contactSecond) {
                        newOuterFace.addAll(path);
                        for (int i = (contactSecond + 1) % faceSize; i != contactFirst; i = (i + 1) % faceSize) {
                            newOuterFace.add(face.get(i));
                        }
                        face2.addAll(path);
                        for (int i = (contactSecond - 1 + faceSize) % faceSize; i != contactFirst; i = (i - 1 + faceSize) % faceSize) {
                            face2.add(face.get(i));
                        }
                    } else {
                        newOuterFace.addAll(reversedPath);
                        for (int i = (contactFirst + 1) % faceSize; i != contactSecond; i = (i + 1) % faceSize) {
                            newOuterFace.add(face.get(i));
                        }
                        face2.addAll(reversedPath);
                        for (int i = (contactFirst - 1 + faceSize) % faceSize; i != contactSecond; i = (i - 1 + faceSize) % faceSize) {
                            face2.add(face.get(i));
                        }
                    }

                    //Удаляем старые, добавляем новые
                    intFaces.remove(extFace);
                    intFaces.add(newOuterFace);
                    intFaces.add(face2);
                    extFace = newOuterFace;
                }
            }
        }

        return new Faces(intFaces, extFace);
    }
}
