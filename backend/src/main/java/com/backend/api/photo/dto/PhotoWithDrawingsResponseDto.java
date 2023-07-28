package com.backend.api.photo.dto;

import com.backend.api.drawing.dto.DrawingFromPhotoDto;
import com.backend.domain.photo.entity.Photo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PhotoWithDrawingsResponseDto {

    private Long photoId;
    private String photoUrl;
    private int pickCnt;
    private int bookmarkCnt;
    private List<DrawingFromPhotoDto> drawings;

    public PhotoWithDrawingsResponseDto(Photo photo) {
        this.photoId = photo.getPhotoId();
        this.photoUrl = photo.getPhotoUrl();
        this.pickCnt = photo.getPickCnt();
        this.bookmarkCnt = photo.getBookmarkCnt();
        this.drawings = photo.getDrawings().stream()
                .map(DrawingFromPhotoDto::new)
                .collect(Collectors.toList());
    }
}
