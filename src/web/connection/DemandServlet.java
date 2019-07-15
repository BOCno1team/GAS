package web.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import matching.Demand;
import matching.MatchResult;

/**
 * Servlet implementation class DemandServlet
 */
@WebServlet(description = "DemandServlet", urlPatterns = { "/demand" })
public class DemandServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DemandServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setHeader("content-type","text/html;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的异域请求都可以接受， */
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		PrintWriter out=response.getWriter();
		// 读取请求内容
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		//这里读出来的字符串时JSON格式的
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		//将json字符串转换为json对象
		System.out.println(sb.toString());
		JSONObject json = JSONObject.parseObject(sb.toString());
		int demanderId = json.getIntValue("DemanderId");//需求方ID
//		System.out.println(json.toJSONString());
//		System.out.println(json.getString("newProvider"));
//		//获取JSON中的内容，此处取id对应的内容
		JSONArray array = JSONObject.parseArray(json.getString("demandFormList"));
		JSONObject obj = null;
		List<Demand> demandList = new ArrayList<>();
		for(int i = 0 ; i < array.size() ; i++){
			obj = array.getJSONObject(i);
			String name = obj.getString("name");
			String category = obj.getString("categoty");
			int id = obj.getIntValue("demandId");
			int amount = obj.getIntValue("amount");
			String unit = obj.getString("unit");
			
			Demand demand = new Demand(id, name, category, amount, unit, demanderId, 0, 0, 10);
			demandList.add(demand);
			demand.uplinkDemand();
		}
		
				
		



		
		
		//System.out.println(array.toString());
		
		//TODO
		/**
		 * 需要在这里加入各种处理逻辑，以前端JSON中获取的内容为输入，调用撮合算法或是信息发布等功能，
		 * 并获取返回值（全部基于java对象进行操作）
		 * 
		 * */
		
		
		
		//组织返回的内容
		json = new JSONObject();
		json.put("demandId", "testId");
		array = new JSONArray();
		JSONObject item = new JSONObject();
		item.put("name","wuzi1");
		item.put("unit","danwei1");
		item.put("amount","shuliang1");
		array.add(item);
		item = new JSONObject();
		item.put("name","wuzi2");
		item.put("unit","danwei2");
		item.put("amount","shuliang2");
		array.add(item);
		json.put("matchResultList", array);
		//将JSON返回前端
		out.append(json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
