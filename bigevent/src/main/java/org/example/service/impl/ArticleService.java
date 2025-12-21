package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.mapper.ArticleMapper;
import org.example.pojo.Article;
import org.example.pojo.PageBean;
import org.example.pojo.User;
import org.example.service.IArticleService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService implements IArticleService {
	@Autowired
	private ArticleMapper articleMapper;
	@Override
	public void add(Article article) {
		User user = (User) ThreadLocalUtil.get();
		article.setCreateUser(user.getId());
		articleMapper.add(article);
	}

	@Override
	public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
		PageBean<Article> pageBean = new PageBean<>();
		User user = ThreadLocalUtil.get();
		PageHelper.startPage(pageNum, pageSize);
		List<Article> list = articleMapper.list(user.getId(), categoryId, state);
		Page<Article> page = (Page<Article>) list;
		pageBean.setTotal(page.getTotal());
		pageBean.setItems(page.getResult());
		return pageBean;
	}
}
