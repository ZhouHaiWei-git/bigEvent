package org.example.mapper;

import lombok.NonNull;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Article;

import java.util.List;

@Mapper
public interface ArticleMapper {

	//添加文章
	@Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
	         "values(#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},now(),now())")
	void add(Article article);

	List<Article> list(@NonNull Integer userId, Integer categoryId, String state);
}
