package de.reinhard.merlin.app.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * A container holds child form entries. The container itself can be managed with siblings if isMultiple. If isMultiple == true
 * the user can add and remove sibling containers.
 */
public class FormContainer extends FormEntry {
    private String title;
    private List<FormEntry> childs;
    private boolean isMultiple;

    public String getTitle() {
        return title;
    }

    public FormContainer setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<FormEntry> getChilds() {
        return childs;
    }

    public void setChilds(List<FormEntry> childs) {
        this.childs = childs;
    }

    /**
     *
     * @param child
     * @return this for chaining.
     */
    public FormContainer addChild(FormEntry child) {
        if (this.childs == null) {
            this.childs = new ArrayList<>();
        }
        this.childs.add(child);
        return this;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    /**
     *
     * @param multiple If true, the user can add and remove sibling containers.
     * @return
     */
    public FormContainer setMultiple(boolean multiple) {
        isMultiple = multiple;
        return this;
    }
}
