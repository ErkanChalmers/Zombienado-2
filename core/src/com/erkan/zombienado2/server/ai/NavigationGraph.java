package com.erkan.zombienado2.server.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.erkan.zombienado2.server.misc.FilterConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Erik on 2018-09-05.
 */
public class NavigationGraph implements IndexedGraph<Vector2> {
    final World world;

    List<Vector2> nodes = new ArrayList<Vector2>();
    List<List<Integer>> edges = new ArrayList<>();
    List<Integer> modified = new ArrayList<>();
    int node_count = 0;

    boolean constructed = false;

    //DEBUG visible Nodes
    public List<Vector2> visible_nodes;

    public NavigationGraph(World world){
        this.world = world;
    }

    public void addNode(Vector2 node){
        if (constructed){
            postAdd(node);
            return;
        }

        boolean add = true;
        for (Vector2 point : nodes){
            if (node.cpy().sub(point).len() < .4f){
                add = false;
            }
        }
        if (add)
            nodes.add(node);

    }

    private synchronized void postAdd(Vector2 node){
        int new_index = nodes.size();
        List<Integer> new_edges = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++){
            if (rayTest(node, nodes.get(i))){
                new_edges.add(i);
                edges.get(i).add(new_index);
                modified.add(i);
            }
        }

        nodes.add(node);
        edges.add(new_edges);

    }

    public synchronized void removeModified(){
        for (Integer index: modified) {
            List<Integer> edg = edges.get(index);
            edg.removeIf(n -> n >= node_count);
        }
        while (nodes.size() > node_count){
            nodes.remove(nodes.size() - 1);
            edges.remove(edges.size() - 1);
        }
        modified.clear();
    }

    /**
     * debug only. Heavy to run
     * @return
     */
    public List<Vector2> getNodes(){
        List<Vector2> list = new ArrayList<>();
        for (int i = 0; i < node_count; i++){
            list.add(nodes.get(i));
        }
        return list;
    }


    boolean vis;
    public boolean rayTest(Vector2 n1, Vector2 n2){
        if (n1.equals(n2)) {
            System.err.println("Navigatable graph: Ray testing node with itself!");
            System.err.println(n1+", "+n2);
            return false;
        }

        vis = true;
        world.rayCast((fixture, point, normal, fraction) -> {
            if (fixture.getFilterData().categoryBits == FilterConstants.OBSTACLE_FIXTURE){
                vis = false;
                return 0;
            }
            return 1;
        }, n1, n2);
        return vis;
    }

/*
    public List<Vector2> getVisibleNodes(Vector2 origin){
        List<Vector2> visible = new ArrayList<>();
        for (Vector2 node : nodes){
            vis = true;

            world.rayCast((fixture, point, normal, fraction) -> {
                if (fixture.getFilterData().categoryBits == FilterConstants.OBSTACLE_FIXTURE){
                    vis = false;
                    return 0;
                }
                return 1;
            }, origin.cpy(), node.cpy());
            if (vis)
                visible.add(node);
        }
        visible_nodes = visible;
        return visible;
    } */

    public void construct(){
        Iterator<Vector2> itr = nodes.iterator();
        while (itr.hasNext()){
            Vector2 point = itr.next();
            world.QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(Fixture fixture) {
                    itr.remove();
                    return false;
                }
            },point.x, point.y, point.x, point.y);

        }


        for (int i = 0; i < nodes.size(); i++){
            edges.add(new ArrayList<>());
            for (int j = 0; j < nodes.size(); j++){
                if (i==j)
                    continue;

                final int from = i;
                final Integer to = j;
                edges.get(from).add(to);
                world.rayCast((fixture, point, normal, fraction) -> {
                    if (fraction <= 1 && fixture.getFilterData().categoryBits == FilterConstants.OBSTACLE_FIXTURE) {
                            edges.get(from).remove(to);
                        return 0;
                    } else {
                        return 1;
                    }
                }, nodes.get(i), nodes.get(j));
            }
        }

        node_count = nodes.size();
        constructed = true;
    }

    @Override
    public int getIndex(Vector2 node) {
        return nodes.indexOf(node);
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public Array<Connection<Vector2>> getConnections(Vector2 fromNode) {
        int index = nodes.indexOf(fromNode);

        List<Integer> list_edges = this.edges.get(index);
        Array<Connection<Vector2>> edges = new Array<>();
        for (Integer i: list_edges) {
            edges.add(new DefaultConnection<>(fromNode, nodes.get(i)));
        }
        return edges;
    }
}
