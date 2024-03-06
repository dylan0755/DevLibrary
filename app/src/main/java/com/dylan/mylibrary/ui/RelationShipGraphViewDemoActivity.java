package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.GsonUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.bean.Customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;

public class RelationShipGraphViewDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationshipgraph);

        try {
            String json=FileUtils.getStringFromInputStream(getAssets().open("relationship.json"));
            List<Customer> customerList=GsonUtils.jsonToList(json,Customer.class);
            createNodeList(customerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNodeList(List<Customer> list) {
        Graph graph=new Graph();
        //创建节点
        HashMap<Integer, Node> fullNodeMap=new HashMap<>();
        HashMap<Integer,List<Node>> childMap=new HashMap<>();
        for (int i = 0; i < list.size() ; i++) {
            Customer customer= list.get(i);
            int parentId=customer.getParentId();
            Node node=new Node(customer);
            fullNodeMap.put(customer.getId(),node);
            if (parentId!=0){
                List<Node> childList=childMap.get(customer.getParentId());
                if (childList==null)childList=new ArrayList<>();
                childList.add(node);
                childMap.put(parentId,childList);
            }
        }
        //绘制节点和其子节点
        Iterator<Map.Entry<Integer,List<Node>>> iterator=childMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,List<Node>> entry=iterator.next();
            int id=entry.getKey();
            List<Node> childList=entry.getValue();
            Node parentCustomer=fullNodeMap.get(id);
            if (parentCustomer==null){
                continue;
            }
            for (Node child:childList) {
                graph.addEdge(parentCustomer,child);
            }
        }
        //绘制剩下没有子节点的节点
        Iterator<Map.Entry<Integer,Node>> parentIterator=fullNodeMap.entrySet().iterator();
        while(parentIterator.hasNext()){
            Map.Entry<Integer,Node> entry=parentIterator.next();
            if (!graph.contains(entry.getValue())){
                graph.addNode(entry.getValue());
            }
        }
        GraphUtils graphUtils=new GraphUtils();
        graphUtils.setupGraphView(findViewById(R.id.recycler),graph);
    }
}
