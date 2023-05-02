![택플 배너](https://user-images.githubusercontent.com/68229193/233971674-22f9b424-1d39-49c6-9a4e-04ecfe10d29c.png)     
## 앱 소개

- 페이지 방식의 동영상 플레이어
- 태그를 통한 동영상 관리
- Dynamic color 및 다크 모드 지원 및 다양한 크기에 맞게 UI 구현

### 동영상 목록

<img width="50%" src="https://user-images.githubusercontent.com/68229193/234310039-80826eb7-16cf-4d48-8efe-376e11aaf3d4.png"/>

- 검색, 필터, 정렬 지원

### 동영상 정보 보기, 태그 설정

<img width="50%" src="https://user-images.githubusercontent.com/68229193/234310053-abba8c7e-86e0-46a2-9fcc-ca3c586114e5.png"/>

- 다중 선택 지원
- 동영상 정보 보기
- 태그 설정

### 동영상 플레이어

<img width="50%" src="https://user-images.githubusercontent.com/68229193/234310098-3b54b83a-4cf9-492a-bc02-8a24ea82de86.png"/>

- 스와이프를 통한 동영상 교체

### 태그 목록

<img width="50%" src="https://user-images.githubusercontent.com/68229193/234310150-02b12612-e44a-4589-a466-54b73358ab72.png"/>

- 태그를 통한 동영상 관리

### 다크 모드

<img width="50%" src="https://user-images.githubusercontent.com/68229193/234310179-dc43ae6f-79d6-4473-8344-8d3347834ac4.png"/>

- 앱 설정을 통한 다크 모드

## 기술 스킬
- Kotlin
- Compose
- Material3
- Hilt
- Navigation
- Room
- Proto Datastore
- Glide
- ExoPlayer(Media3)
- Junit4
- Mockito
- SplashScreen API
- Palette API

## 주요 기능

### Adaptive Layout
<img width="40%" src="https://user-images.githubusercontent.com/68229193/235183610-032bcbf9-885e-45e9-ab15-1e7fad854a6c.png"/> <img width="40%" src="https://user-images.githubusercontent.com/68229193/235185440-c454c029-4207-4050-970d-0fdce91bf39e.png"/>

- 화면 크기에 따라 UI 변경되게 구현
- 화면 타입 객체 구현 및 해당 객체를 반환하는 Composable 구현
    
### Dynamic Color
<img width="50%" src="https://user-images.githubusercontent.com/68229193/234827415-411a361a-7f15-46d4-8ed8-2fc37527bd23.png"/>

- M3 Theme colorScheme를 사용해서 앱 UI 구현
- 디바이스 설정 컬러 팔레트 선택과 다크 모드 유무에 따라 색이 동적으로 표시
- Palette API를 사용해 동영상 섬네일 색 추출, 해당 색을 섬네일 바탕색으로 사용

### Animation

<img width="40%" src="https://user-images.githubusercontent.com/68229193/235626478-685214e5-8750-4cda-88b1-eb8c03f9c3c9.gif"/> <img width="40%" src="https://user-images.githubusercontent.com/68229193/235190074-b0801859-5dee-4446-b21b-cb9d0eda29d8.gif"/>
<img width="30%" src="https://user-images.githubusercontent.com/68229193/235190084-19512aae-69e1-4cea-b5e6-21414ca916fa.gif"/> <img width="30%" src="https://user-images.githubusercontent.com/68229193/235190091-49d98ea1-bb99-4415-9b5b-f1cd27cabaee.gif"/> <img width="30%" src="https://user-images.githubusercontent.com/68229193/235190031-65d479cf-9d03-4a0d-94e5-1e5564569266.gif"/>

- `InfiniteTransition`를 사용해 로딩 애니메이션 구현
- `AnimationVisibility`를 사용해 바텀바 Slide 애니메이션과 필터 칩 leading icon 애니메이션 구현
- `animateFloatAsState`를 사용해 동영상 플레이어 진행바 구현

### Pager, Glide, ExoPlayer를 활용한 동영상 플레이어
<img width="40%" src="https://user-images.githubusercontent.com/68229193/235626397-b7d4e068-e51d-49de-a565-d3b706f1463e.gif"/>

- ExoPlayer로 동영상 재생, `LifecycleEventObserver`를 통해 앱이 백그라운드 시 동영상 정지되게 구현
- View 기반인 ExoPlayer의 `PlayerView`는 `AndroidView`로 구현, 컨트롤러 UI는 Compose로 직접 구현
- 동영상 플레이어 페이지 넘기는 방식은 `androidx.compose.foundation.pager`를 통해 구현
- 현재 사용자에게 보여지는 페이지가 아니더라도 동영상을 준비시키고 `PlayerView`를 생성하는 것을 막기 위해
Pager의 페이지가 `settledPage`가 아니면 Glide를 사용한 첫 프레임 이미지를 표시하고, `settledPage`일 경우 동영상을 표시

### Room

- 디바이스내 동영상 정보와 사용자가 생성한 태그를 Room에 저장
- 동영상 정보와 태그는 다대다 관계이며, 두 Entity를 연결하는 associate entity `TagVideoCrossRef`구현
- 동영상 정보와 태그를 id를 `TagVideoCrossRef`의 ForeignKey로 지정하고 `onDelete`를 `ForeignKey.CASCADE`로 설정해 동영상 정보나 태그가 삭제될 시 해당 entity의 `TagVideoCrossRef`를 삭제하게 구현
- 태그 entity의 이름을 index로 지정하고, unique로 적용해 태그 이름 중복 방지 

### Proto Datastore

- 동영상 목록에 적용되는 필터, 정렬과 앱 테마 설정을 Proto Datastore로 저장
- Proto Datastore는 Flow를 지원하며, 유형 안정성을 제공하기 때문에 사용

## 개발 일지
[App Ui Design](https://www.figma.com/file/q0WPhwmHwXlT5b4ZCjm09y/TagPlayer?node-id=0%3A1&t=SfqGdnrJIruIhwqg-1)

[Compose에서 동영상 썸네일 이미지 Coil, Glide 비교](https://childish-lynx-d23.notion.site/Compose-Coil-Glide-d3b658cb59e94ba2aba3e0f0c875ea4c)
