package com.songm.dormrepair.model.orderinfo;

import java.util.List;

/**
 * Created by SongM on 2017/9/17.
 * 报修单的评价
 */

public class Eval {

    private List<EvalListBean> evalList;

    public List<EvalListBean> getEvalList() {
        return evalList;
    }

    public void setEvalList(List<EvalListBean> evalList) {
        this.evalList = evalList;
    }

    public static class EvalListBean {
        /**
         * evalName : 宋师傅
         * evalContent : 我觉得OK！
         * evalIconUrl : imgs/2017_09_22/20_47_03/u=2323194313,3449826299&fm=27&gp=0.jpeg
         */

        private String evalName;
        private String evalContent;
        private String evalIconUrl;

        public String getEvalName() {
            return evalName;
        }

        public void setEvalName(String evalName) {
            this.evalName = evalName;
        }

        public String getEvalContent() {
            return evalContent;
        }

        public void setEvalContent(String evalContent) {
            this.evalContent = evalContent;
        }

        public String getEvalIconUrl() {
            return evalIconUrl;
        }

        public void setEvalIconUrl(String evalIconUrl) {
            this.evalIconUrl = evalIconUrl;
        }
    }
}
