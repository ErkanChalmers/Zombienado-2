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

    //DEBUG visible Nodes
    public List<Vector2> visible_nodes;

    public NavigationGraph(World world){
        this.world = world;
    }

    public void addNode(Vector2 node){
        boolean add = true;
        for (Vector2 point : nodes){
            if (node.cpy().sub(point).len() < .4f){
                add = false;
            }
        }
        if (add)
            nodes.add(node);

    }

    public List<Vector2> getNodes(){
        return nodes;
    }

    boolean vis;
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
    }

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
