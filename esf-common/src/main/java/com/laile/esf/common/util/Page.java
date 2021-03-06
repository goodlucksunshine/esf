package com.laile.esf.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页类
 *
 * @author FH QQ []
 * 创建时间：2014年6月28日
 */
public class Page<E> {

    private int showCount; //每页显示记录数
    private int totalPage;        //总页数
    private int totalResult;    //总记录数
    private int currentPage;    //当前页
    private int currentResult;    //当前记录起始索引
    private String pageStr;        //最终页面显示的底部翻页导航，详细见：getPageStr();
    private String pageContext;
    private List<E> resultList = new ArrayList<E>();

    public List<E> getResultList() {
        return resultList;
    }

    public void setResultList(List<E> resultList) {
        this.resultList = resultList;
    }

    public Page() {
        try {
//            this.showCount = Integer.parseInt(Tools.readTxtFile(Constant.PAGE));
            this.showCount = 10;
        } catch (Exception e) {
            this.showCount = 15;
        }
    }

    public Page(Integer currentPage, Integer showCount) {
        if (currentPage == null) {
            this.currentPage = 0;
        } else {
            this.currentPage = currentPage;
        }
        if (showCount == null || showCount == 0) {
            this.showCount = 10;
        } else {
            this.showCount = showCount;
        }
    }

    public int getTotalPage() {
        if (totalResult % showCount == 0)
            totalPage = totalResult / showCount;
        else
            totalPage = totalResult / showCount + 1;
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public int getCurrentPage() {
        if (currentPage <= 0)
            currentPage = 1;
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }


    //拼接分页 页面及JS函数
    public String getPageStr() {
        StringBuffer sb = new StringBuffer();
        if (totalResult > 0) {
            sb.append("	<ul class=\"pagination pull-right no-margin\">\n");
            if (currentPage == 1) {
                sb.append("	<li><a>共<font color=red>" + totalResult + "</font>条</a></li>\n");
                sb.append("	<li><input type=\"number\" value=\"\" id=\"toGoPage\" style=\"width:50px;text-align:center;float:left\" placeholder=\"页码\"/></li>\n");
                sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"toTZ();\"  class=\"btn btn-mini btn-success\">跳转</a></li>\n");
                sb.append("	<li><a>首页</a></li>\n");
                sb.append("	<li><a>上页</a></li>\n");
            } else {
                sb.append("	<li><a>共<font color=red>" + totalResult + "</font>条</a></li>\n");
                sb.append("	<li><input type=\"number\" value=\"\" id=\"toGoPage\" style=\"width:50px;text-align:center;float:left\" placeholder=\"页码\"/></li>\n");
                sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"toTZ();\"  class=\"btn btn-mini btn-success\">跳转</a></li>\n");
                sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"nextPage(1)\">首页</a></li>\n");
                sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"nextPage(" + (currentPage - 1) + ")\">上页</a></li>\n");
            }
            int showTag = 5;//分页标签显示数量
            int startTag = 1;
            if (currentPage > showTag) {
                startTag = currentPage - 1;
            }
            int endTag = startTag + showTag - 1;
            for (int i = startTag; i <= totalPage && i <= endTag; i++) {
                if (currentPage == i)
                    sb.append("<li class=\"active\"><a><font color='white'>" + i + "</font></a></li>\n");
                else
                    sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"nextPage(" + i + ")\">" + i + "</a></li>\n");
            }
            if (currentPage == totalPage) {
                sb.append("	<li><a>下页</a></li>\n");
                sb.append("	<li><a>尾页</a></li>\n");
            } else {
                sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"nextPage(" + (currentPage + 1) + ")\">下页</a></li>\n");
                sb.append("	<li style=\"cursor:pointer;\"><a onclick=\"nextPage(" + totalPage + ")\">尾页</a></li>\n");
            }
            sb.append("	<li><a>第" + getCurrentPage() + "/共" + getTotalPage() + "页</a></li>\n");
            sb.append("	<li><select title='显示条数' style=\"width:55px;float:left;margin-top:1px;\" onchange=\"changeCount(this.value)\">\n");
            sb.append("	<option value='" + showCount + "'>" + showCount + "</option>\n");
            sb.append("	<option value='10'>10</option>\n");
            sb.append("	<option value='20'>20</option>\n");
            sb.append("	<option value='30'>30</option>\n");
            sb.append("	<option value='40'>40</option>\n");
            sb.append("	<option value='50'>50</option>\n");
            sb.append("	<option value='60'>60</option>\n");
            sb.append("	<option value='70'>70</option>\n");
            sb.append("	<option value='80'>80</option>\n");
            sb.append("	<option value='90'>90</option>\n");
            sb.append("	<option value='99'>99</option>\n");
            sb.append("	</select>\n");
            sb.append("	</li>\n");
        }
        pageStr = sb.toString();
        return pageStr;
    }

    public void setPageStr(String pageStr) {
        this.pageStr = pageStr;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {

        this.showCount = showCount;
    }

    public String getPageContext() {
        StringBuffer sb = new StringBuffer();
        if (totalResult > 0) {
            if (currentPage == 0) {
                currentPage = 1;
            }
            sb.append("	<ul class=\"am-pagination tpl-pagination\"><li class=\"am-disabled\"><a href=\"getNextPage(" + (currentPage - 1) + ")\">«</a></li>");
            int startPage = 0;
            int endPage = getTotalPage();
            if (getTotalPage() <= 5) {
                startPage = 1;
            } else {
                if (currentPage - 2 <= 1) {
                    startPage = 1;
                } else {
                    startPage = currentPage - 2;
                }
                if (currentPage + 2 > getTotalPage()) {
                    endPage = getTotalPage();
                } else {
                    endPage = currentPage + 2;
                    if (endPage < 5) {
                        endPage = 5;
                    }
                }
            }

            for (int i = startPage; i <= endPage; i++) {
                if (i == currentPage) {
                    sb.append("<li><a class=\"am-active\" href=\"#\" onclick=\"nextPage(" + i + ")\">" + i + "</a></li>");
                } else {
                    sb.append("<li><a href=\"#\" onclick=\"nextPage(" + i + ")\">" + i + "</a></li>");
                }
            }
            sb.append("<li><a  href=\"javascript:nextPage(" + (currentPage + 1) + ")\">»</a></li></ul>");
        }
        pageContext = sb.toString();
        return pageContext;
    }

    public void setPageContext(String pageContext) {
        this.pageContext = pageContext;
    }

    public int getCurrentResult() {
        currentResult = (getCurrentPage() - 1) * getShowCount();
        if (currentResult < 0)
            currentResult = 0;
        return currentResult;
    }

    public void setCurrentResult(int currentResult) {
        this.currentResult = currentResult;
    }

    @Override
    public String toString() {
        return "Page{" +
                "showCount=" + showCount +
                ", totalPage=" + totalPage +
                ", totalResult=" + totalResult +
                ", currentPage=" + currentPage +
                ", currentResult=" + currentResult +
                ", pageStr='" + pageStr + '\'' +
                ", resultList=" + resultList +
                '}';
    }
}
