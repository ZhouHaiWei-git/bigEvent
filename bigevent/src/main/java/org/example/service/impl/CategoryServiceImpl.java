package org.example.service.impl;

import org.example.mapper.CategoryMapper;
import org.example.pojo.Category;
import org.example.pojo.User;
import org.example.service.ICategoryService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
	@Autowired
	private CategoryMapper categoryMapper;
	@Override
	public void add(Category category) {
		User user = (User) ThreadLocalUtil.get();
		category.setCreateUser(user.getId());
		categoryMapper.add(category);
	}

	@Override
	public List<Category> list() {
		User user = (User) ThreadLocalUtil.get();
		return categoryMapper.list(user.getId());
	}

	@Override
	public Category findById(Integer id) {
		return categoryMapper.findById(id);
	}

	@Override
	public void update(Category category) {
		categoryMapper.update(category);
	}

	@Override
	public void delete(Integer id) {
		categoryMapper.delete(id);
	}
}
