/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public final class TreeNode<E> {
    
    /**
     * The element stored in this tree node.
     */
    private E element;
    
    /**
     * The parent node of this tree node.
     */
    private TreeNode<E> parent;
    
    /**
     * A list of the child nodes of this tree node.
     */
    private List<TreeNode<E>> children;
    
    /**
     * Constructor
     * @param element the element of this tree node.
     */
    public TreeNode(E element) {
        this.element = element;
        this.parent = null;
        this.children = new ArrayList();
    }
    
    /**
     * Adds a child tree node to this tree node.
     * @param element the new child element.
     * @return the newly created child node.
     */
    public TreeNode<E> addChild(E element) {
        TreeNode<E> child = new TreeNode<>(element);
        child.parent = this;
        children.add(child);
        return child;
    }
    
    /**
     * Returns the children of this tree node.
     * @return the children of this tree node. 
     */
    public List<TreeNode<E>> getChildren() {
        return children;
    }
    
    /**
     * Gets the latest child added to the tree node.
     * @return the latest child added to the tree node.
     */
    public TreeNode<E> getLast() {
        return children.get(children.size()-1);
    }
    
    /**
     * Gets the element from this tree node.
     * @return 
     */
    public E getElement() {
        return element;
    }

    /**
     * Sets the element of this tree node.
     * @param element 
     */
    public void setElement(E element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return Objects.toString(element);
    }
    
}
