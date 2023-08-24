package com.garcheng.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 品牌
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 00:25:41
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotEmpty(message = "姓名不能为空！")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotNull(message = "logo地址不能为空！")
	@URL(message = "不是一个合法的url地址！")
	private String logo;
	/**
	 * 介绍
	 */
	@NotNull(message = "描述地址不能为空！")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "显示状态不能为空！")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotNull(message = "检索的首字母不能为空！")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空！")
	private Integer sort;

}
