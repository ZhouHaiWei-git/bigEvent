package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.example.pojo.FileInfo;

@Mapper
public interface FileInfoMapper {

	@Insert("insert into file_info(original_name,stored_name,relative_path,content_type,size,sha256,create_user,create_time) " +
			"values(#{originalName},#{storedName},#{relativePath},#{contentType},#{size},#{sha256},#{createUser},now())")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(FileInfo fileInfo);

	@Select("select * from file_info where id = #{id}")
	FileInfo findById(Long id);
}
