package com.recipia.recipe.config.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * S3 이미지 저장 서비스
 * 도움받은 블로그(코드) 출처: https://growth-coder.tistory.com/116
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ImageS3Service {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름

    /**
     * 이미지를 S3에 업로드하고 저장된 이미지의 url을 반환한다.
     */
    private String uploadImageToS3(MultipartFile image, String ext, String storedFileName) {

        //putObject 인자로 들어갈 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        // 이미지, 파일을 받으려면 파일 형식에 맞게 contentType을 전부 지정해 준다.
        metadata.setContentType("image/"+ext.substring(1));

        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, storedFileName, image.getInputStream(), metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new RecipeApplicationException(ErrorCode.S3_UPLOAD_ERROR);
        }
        // 업로드에 성공하면 AmazonS3의 getUrl 메소드를 사용하여 저장된 url 반환
        return amazonS3.getUrl(bucketName, storedFileName).toString();
    }


    /**
     * 데이터베이스에 저장할 RecipeFileEntity 객체를 생성하여 반환한다.
     */
    public RecipeFileEntity createRecipeFileEntity(MultipartFile image, Long savedRecipeId){

        String originFileName = image.getOriginalFilename(); //원본 이미지 이름

        // 확장자 추출 과정에서의 안전한 처리
        String fileExtension = "";
        if (originFileName != null && originFileName.contains(".")) {
            fileExtension = originFileName.substring(originFileName.lastIndexOf("."));
        }

        // S3에 이미지 업로드 및 URL 추출
        String storedFileName = changedImageName(fileExtension); // 새로 생성된 이미지 이름
        String storedFilePath = uploadImageToS3(image, fileExtension, storedFileName); // 저장된 이미지의 URL
        Integer fileSize = (int) image.getSize(); // 파일 사이즈 추출

        return RecipeFileEntity.saveFileEntity(
                RecipeEntity.of(savedRecipeId), // 연관 레시피
                storedFilePath,                 // s3에 저장된 이미지 URL
                originFileName,                 // 파일 원본 이름
                storedFileName,                 // s3에 저장된 파일 이름
                fileExtension,                  // 확장자
                fileSize                        // 사이즈
        );
    }

    /**
     * s3에 이미지를 저장할때 UUID와 함께 저장되도록 만들어준다.
     * 중복된 이름을 제거하기 위함
     */
    private String changedImageName(String ext) {
        String random = UUID.randomUUID().toString();
        return random+ext;
    }

    /**
     * 이미지(파일)를 삭제한다.
     */
    public void deleteImage(String key) {
        DeleteObjectRequest deleteRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteRequest);
    }

}
