import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vyiyun.weixin.utils.HttpsUtil;

public class ShowHttpHeaders {

	// 要查找的网址
	private ArrayList<String> websites;

	/**
	 * 构造函数
	 * 
	 * @param websites
	 *            网站列表
	 */
	public ShowHttpHeaders(ArrayList<String> websites) {
		this.websites = websites;
	}

	/**
	 * 获取响应头 打印到控制台
	 */
	public void getHeaders() {
		if (websites == null) {
			System.err.println("查询网址不能为空！");
			return;
		}
		try {
			for (String website : websites) {
				System.out.println("----------------网站：" + website + "HTTP响应头---------------");
				URL url = new URL(website);
				URLConnection connection = url.openConnection();
				Map<String, List<String>> headerFields = connection.getHeaderFields();
				Set<Entry<String, List<String>>> entrySet = headerFields.entrySet();
				Iterator<Entry<String, List<String>>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, List<String>> next = iterator.next();
					String key = next.getKey();
					List<String> value = next.getValue();
					if (key == null)
						System.out.println(value.toString());
					else
						System.out.println(key + ":" + value.toString());
				}
				System.out.println("");
			}
		} catch (IOException e) {
			System.err.println("无法查询网址！");
		}
	}
	public static void main(String[] args) {
       
		String web1="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx9a6ed51615d003de&redirect_uri=http://shan.3w.net579.com/oauth2url.do.action&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
       
        ArrayList<String> websites=new ArrayList<String>();
        websites.add(web1);
      
        ShowHttpHeaders showHttpHeaders = new ShowHttpHeaders(websites);
        showHttpHeaders.getHeaders();
         
    }
}
