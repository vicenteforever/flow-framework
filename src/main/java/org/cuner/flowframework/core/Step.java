package org.cuner.flowframework.core;

import org.apache.commons.collections.CollectionUtils;
import org.cuner.flowframework.core.transition.Transition;
import org.cuner.flowframework.core.transition.condition.Condition;

import java.util.List;

/**
 * Created by houan on 18/7/24.
 */
public class Step {

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 是否异步执行
     */
    private boolean asyn = false;

    /**
     * 执行条件
     */
    private Condition condition;

    /**
     * 步骤执行的操作
     */
    private Action action;

    /**
     * 步骤代表的子流程
     */
    private Flow subFlow;

    /**
     * 该步骤执行结束后的符合所有条件的流转信息(因为什么条件流程到什么步骤)
     */
    private List<Transition> transitionList;

    /**
     * 通过上下文获取符合条件的流转信息
     * @param context
     * @return
     */
    public Transition getMatchedTransition(FlowContext context) {
        if (CollectionUtils.isEmpty(transitionList)) {
            return null;
        }

        for (Transition transition : transitionList) {
            if (transition.match(context)) {
                return transition;
            }
        }

        return null;
    }

    /**
     * 获取下一个步骤
     * @param flowContext
     * @return
     */
    public Step next(FlowContext flowContext) {
        if (CollectionUtils.isEmpty(transitionList)) {
            return null;
        }

        for (Transition transition : transitionList) {
            if (transition.match(flowContext)) {
                return transition.getTarget(flowContext);
            }
        }

        return null;
    }

    /**
     * 步骤执行
     * @param context
     */
    @SuppressWarnings("all")
    public void execute(FlowContext context) {
        if (condition != null && !condition.match(context)) {
            return;
        }

        if (action != null) {
            context.setStep(this);
            action.execute(context.createFlowContext());
        } else if (subFlow != null) {
            FlowContext copiedContext = context.copy();
            copiedContext.setFlow(subFlow);
            copiedContext.setStep(null);
            copiedContext.setAsyn(this.asyn);
            subFlow.execute(copiedContext);
        }

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAsyn() {
        return asyn;
    }

    public void setAsyn(boolean asyn) {
        this.asyn = asyn;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Flow getSubFlow() {
        return subFlow;
    }

    public void setSubFlow(Flow subFlow) {
        this.subFlow = subFlow;
    }

    public List<Transition> getTransitionList() {
        return transitionList;
    }

    public void setTransitionList(List<Transition> transitionList) {
        this.transitionList = transitionList;
    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", asyn=" + asyn +
                ", condition=" + condition +
                ", action=" + action +
                ", subFlow=" + subFlow +
                ", transitionList=" + transitionList +
                '}';
    }
}
