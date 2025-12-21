package org.example.colltroller;

import org.example.pojo.Article;
import org.example.pojo.PageBean;
import org.example.pojo.Result;
import org.example.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {
	@Autowired
	private IArticleService articleService;

	@PostMapping
	public Result add(@RequestBody @Validated Article article) {
		articleService.add(article);
		return Result.success();
	}
	@GetMapping
    public Result<PageBean<Article>> list(Integer pageNum, Integer pageSize, Integer categoryId,String  state){
		PageBean<Article> pageBean = articleService.list(pageNum, pageSize, categoryId, state);
		return Result.success(pageBean);
	}
}
