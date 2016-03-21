import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class Test {

	public static void main(String[] args) throws InterruptedException {

		/*
		 * String[] servers = { "114.215.202.143:11211"}; SockIOPool pool =
		 * SockIOPool.getInstance(); pool.setServers( servers );
		 * pool.setFailover( true ); pool.setInitConn( 10 ); pool.setMinConn( 5
		 * ); pool.setMaxConn( 250 ); pool.setMaintSleep( 30 ); pool.setNagle(
		 * false ); pool.setSocketTO( 3000 ); pool.setAliveCheck( true );
		 * pool.initialize();
		 */

		// MemCachedClient memCachedClient = new MemCachedClient();

		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "config/spring-memcache.xml" });

		SockIOPool s = (SockIOPool) ctx.getBean("memcachedPool");
		// System.out.println("s=" + s.getInitConn());

		MemCachedClient memCachedClient = (MemCachedClient) ctx.getBean("memcachedClient");
		// 默认一分钟
		memCachedClient.set("hello", "12345", new Date(60 * 1000));
		for (int i = 0; i < 10; i++) {
			// boolean success = memCachedClient.set( "" + i, "Hello!" );
			String result = (String) memCachedClient.get("" + i);
			// System.out.println( String.format( "set( %d ): %s", i, success )
			// );
			System.out.println(String.format("--------get( %d ): %s", i, result));
		}
		System.out.println((new Date(60000).getTime() / 1000));
		for (int i = 0; i < 10; i++) {
			// boolean success = memCachedClient.set( "" + i, "Hello!" );
			String result = (String) memCachedClient.get("" + i);
			// System.out.println( String.format( "set( %d ): %s", i, success )
			// );
			System.out.println(String.format("--------get( %d ): %s", i, result));
		}
		String result = (String) memCachedClient.get("hello");
		System.out.println("-----------" + result);
	}
}
