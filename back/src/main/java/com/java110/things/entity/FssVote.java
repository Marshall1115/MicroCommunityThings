package com.java110.things.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


public class FssVote extends PageDto implements Serializable{
   private static final long serialVersionUID = 1L;
	
   /****/
   private String id;
	
   /**投票id**/
   private String title1;
	
   /**标题id**/
   private String title2;
	
   /**标题id**/
   private String title3;
	
   /**选项id**/
   private String building;
	
   /**答选项内容**/
   private String room;
	
   /**备注**/
   private String tel;
	
   /****/
   private String name;
	
   /****/
   private Date createTime;
	
   /**投票ip**/
   private String fromIp;
	
   /**投票开始时间**/
   private Date orderDate;
	

   public FssVote() {
      // NOOP
   }


   /**
    * 设置：
    */
   public void setId(String id) {
      this.id = id;
   }
	
   /**
	* 获取：
	*/
   public String getId() {
      return id;
   }
	
   /**
    * 设置：投票id
    */
   public void setTitle1(String title1) {
      this.title1 = title1;
   }
	
   /**
	* 获取：投票id
	*/
   public String getTitle1() {
      return title1;
   }
	
   /**
    * 设置：标题id
    */
   public void setTitle2(String title2) {
      this.title2 = title2;
   }
	
   /**
	* 获取：标题id
	*/
   public String getTitle2() {
      return title2;
   }
	
   /**
    * 设置：标题id
    */
   public void setTitle3(String title3) {
      this.title3 = title3;
   }
	
   /**
	* 获取：标题id
	*/
   public String getTitle3() {
      return title3;
   }
	
   /**
    * 设置：选项id
    */
   public void setBuilding(String building) {
      this.building = building;
   }
	
   /**
	* 获取：选项id
	*/
   public String getBuilding() {
      return building;
   }
	
   /**
    * 设置：答选项内容
    */
   public void setRoom(String room) {
      this.room = room;
   }
	
   /**
	* 获取：答选项内容
	*/
   public String getRoom() {
      return room;
   }
	
   /**
    * 设置：备注
    */
   public void setTel(String tel) {
      this.tel = tel;
   }
	
   /**
	* 获取：备注
	*/
   public String getTel() {
      return tel;
   }
	
   /**
    * 设置：
    */
   public void setName(String name) {
      this.name = name;
   }
	
   /**
	* 获取：
	*/
   public String getName() {
      return name;
   }
	
   /**
    * 设置：
    */
   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }
	
   /**
	* 获取：
	*/
   public Date getCreateTime() {
      return createTime;
   }
	
   /**
    * 设置：投票ip
    */
   public void setFromIp(String fromIp) {
      this.fromIp = fromIp;
   }
	
   /**
	* 获取：投票ip
	*/
   public String getFromIp() {
      return fromIp;
   }
	
   /**
    * 设置：投票开始时间
    */
   public void setOrderDate(Date orderDate) {
      this.orderDate = orderDate;
   }
	
   /**
	* 获取：投票开始时间
	*/
   public Date getOrderDate() {
      return orderDate;
   }
	
}
