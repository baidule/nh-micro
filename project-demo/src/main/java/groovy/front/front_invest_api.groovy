package groovy.front

import com.nh.micro.rule.engine.core.GInputParam;
import com.nh.micro.rule.engine.core.GOutputParam;
import com.nh.micro.rule.engine.core.GContextParam;
import com.nh.micro.rule.engine.core.GroovyExecUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.awt.event.ItemEvent;
import java.sql.PreparedStatement;
import groovy.json.*;
import com.nh.micro.db.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.nh.micro.cache.base.*;
import com.nh.micro.db.Cutil;
import com.nh.micro.db.Cobj;
import com.nh.micro.db.MicroDbHolder;


import org.springframework.jdbc.support.rowset.*;
import groovy.template.MicroMvcTemplate;
import javax.servlet.http.HttpSession;

class FrontProduct extends MicroMvcTemplate{
public String pageName="listDictionaryInfo";
public String tableName="t_front_product";


public String getPageName(HttpServletRequest httpRequest){
	return pageName;
}
public String getTableName(HttpServletRequest httpRequest){
	return tableName;
}

public void investProduct(GInputParam gInputParam,GOutputParam gOutputParam,GContextParam gContextParam){
	
	HttpServletRequest httpRequest = gContextParam.getContextMap().get("httpRequest");
	HttpSession httpSession=gContextParam.getContextMap().get("httpSession");
	String nhUserName=httpSession.getAttribute("nhUserName");
	String tableName=getTableName(httpRequest);
	String pageName=getPageName(httpRequest);
	Map requestParamMap=getRequestParamMap(httpRequest);

	//生成投资记录
	Map investMap=new HashMap();
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String orderNumber=sdf.format(new Date());
	investMap.put("order_number", orderNumber);
	investMap.put("user_code", nhUserName);
	investMap.put("user_name", nhUserName);
	String productCode=requestParamMap.get("productCode");
	String productName=requestParamMap.get("productName");
	investMap.put("bid_name", productName);
	investMap.put("bid_code", productCode);
	investMap.put("product_name", productName);
	investMap.put("product_code", productCode);
	String investAmount=requestParamMap.get("investAmount");
	investMap.put("invest_amount",investAmount);
	String tradeStatus="4";
	investMap.put("trade_status",tradeStatus);
	createInfoService(investMap,"t_front_invest");
	
	//扣除账户金额
	String subAccountSql="update t_front_account set available_balance=available_balance-? where meta_key=?"
	List placeList=new ArrayList();
	placeList.add(investAmount);
	placeList.add(nhUserName);
	updateInfoServiceBySql(subAccountSql,placeList);
	
	//生成投资记录
	Map tranMap=new HashMap();
	SimpleDateFormat sdf_tran=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String tranId=sdf.format(new Date());
	tranMap.put("inner_recharge_number", tranId);
	tranMap.put("recharge_money",investAmount);
	tranMap.put("recharge_user_code",nhUserName);
	tranMap.put("recharge_type","3");
	tranMap.put("recharge_status","1");
	createInfoService(tranMap,"t_front_recharge");

	return;
}
			
}
