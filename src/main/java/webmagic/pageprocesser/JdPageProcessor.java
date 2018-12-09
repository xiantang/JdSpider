package webmagic.pageprocesser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.LinkedList;
import java.util.List;


public class JdPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static final String MAIN_PAGE = "https://www.jd.com/allSort.aspx";
    private static final String PRODUCT_LIST = "https://list.jd.com/list.html?cat=";

    public Site getSite() {
        return site;
    }

    public void process(Page page) {
        if(page.getUrl().regex(MAIN_PAGE).match()){
            crawlList(page);
        }
        if(page.getUrl().toString().indexOf(PRODUCT_LIST)!=-1){
            crawProductList(page);
        }
    }

    //获取所有分类中的所有文章
    private void crawlList(Page page){
        List<String> list= page.getHtml().xpath("//dl[@class=\"clearfix\"]/dd/a/@href").all();
        List<String> filterUrl = new LinkedList<String>();
        String subUrl = "//list.jd.com/list.html?cat=";
        for (int i = 0; i <list.size() ; i++) {
            if(list.get(i).indexOf(subUrl)!=-1){
                filterUrl.add("https:"+list.get(i));
            }
        }
        page.addTargetRequests(filterUrl);

    }

    // 获取商品的所有信息
    private void crawProductList(Page page) {
        List<String> urls=page.getHtml().xpath("//div[@class=\"p-name\"]/a/@href").all();
        for (String url: urls
             ) {

            // 这个接口获取详细信息
            // https://item.m.jd.com/item/mview2?datatype=1&callback=skuInfoCBD&cgi_source=mitem&sku=7293234

            System.out.println(url);
        }

    }

    public static void main(String[] args) {
        Spider.create(new JdPageProcessor()).addUrl("https://www.jd.com/allSort.aspx").thread(5).run();
    }
}