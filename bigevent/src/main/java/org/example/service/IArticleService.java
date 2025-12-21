package org.example.service;

import org.example.pojo.Article;
import org.example.pojo.PageBean;

public interface IArticleService {
	void add(Article article);

	// 分页查询
	PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);
}
