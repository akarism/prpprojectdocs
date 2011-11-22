package edu.sjtu.se.ist.forever.schedule;

public class TreeNode {

    /**
     * 父节点
     */
    TreeNode parent;

    /**
     * 指向父的哪个属性
     */
    String parentArrtibute;

    /**
     * 节点名
     */
    String nodeName;

    /**
     * 属性数组
     */
    String[] arrtibutes;

    /**
     * 节点数组
     */
    TreeNode[] childNodes;
    
    /**
     * 可信度
     */
    double percent;

}  
