package org.example.colltroller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.pojo.Category;
import org.example.pojo.Result;
import org.example.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private ICategoryService categoryService;
	@PostMapping()
	public Result add(@RequestBody @Validated(Category.Add.class) Category category) {
		categoryService.add(category);
		return Result.success();
	}
	@GetMapping
	public Result<List<Category>> list() {
		List<Category> categoryList = categoryService.list();
		return Result.success(categoryList);
	}
	@GetMapping("/detail")
	public Result<Category> detail(@RequestParam Integer id) {
		Category category = categoryService.findById(id);
		return Result.success(category);
	}

	@PutMapping
	public Result update(@RequestBody @Validated(Category.Update.class) Category category) {
		categoryService.update(category);
		return Result.success();
	}

	@DeleteMapping
	public Result delete(@RequestParam @Pattern(regexp = "^\\d+$", message = "id只能是正整数") Integer id) {
		categoryService.delete(id);
		return Result.success();
	}
}
