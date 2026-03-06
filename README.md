# 프로젝트 개요

  MIRUNI는 미루는 습관 개선을 위한 안드로이드 앱입니다. 
  사용자가 할 일을 입력하면 AI가 스케줄을 생성하고 시간을 분배하며, 
  방해금지모드 타이머를 통해 집중을 유도합니다. 
  앱을 이탈하면 푸시 알림으로 복귀를 유도합니다.

<br><br><br>

## 사용자 플로우
```
  스플래시 → (최초) 온보딩 → 로그인/회원가입
           → 홈 화면 (오늘의 일정, 진행률)
              ├─ AI 플래너: 할 일 입력 → AI 스케줄 생성 → 일정 확인/수정
              ├─ 방해금지모드: 온보딩 → 타이머 설정 → 실행 →
  일시정지/조기종료/완료
              ├─ 캘린더: 일정 조회
              └─ 마이페이지: 프로필 편집, 계정 설정, 알림 설정, 피드백
```

## 흐름

```
  UI (Compose Screen)
    ↓ Event 발행
  ViewModel (BaseViewModel<Event, State, Effect>)
    ↓ Repository 호출
  Repository (Interface → Impl)
    ↓ API 호출
  Retrofit API Interface
    ↓ OkHttp + AuthInterceptor (JWT 자동 주입)
  Spring Boot REST API Server
```

  State는 StateFlow로 UI에 반영되고, 
  네비게이션/토스트 등 일회성 동작은 Effect(Channel)로 전달됩니다.

<br><br><br>

# 기술 스택 및 아키텍쳐

## 개발 환경

| IDE | Android Studio |
| 언어 | Kotlin 2.0.20 |
| UI 프레임워크 | Jetpack Compose (BOM 2024.10.00), Material3 1.4.0 |
| 빌드 | AGP 8.9.1, Gradle 8.10.1, KSP 2.0.20  |
| Min SDK / Target SDK | 24 / 36 |

## 네비게이션
  NavigationDestination 인터페이스를 정의하고, 각 Feature 모듈이 이를 구현합니다.

  ```kotlin
  interface NavigationDestination {
      val route: String
      fun register(builder: NavGraphBuilder, navController: NavHostController)
  }
  ```

## 모듈 구조

```
app/                      → DI 모듈, MainActivity, MainScreen,
  BottomNavigationBar
  core/                   → BaseViewModel, NavigationDestination, Route 정의,
                            네트워크 레이어, DataStore, DesignSystem
  (Theme/Color/Type)
  feature/
    ├─ splash/            → 스플래시 화면, 자동 로그인 판단
    ├─ onboard/           → 앱 최초 실행 온보딩
    ├─ login/             → 이메일/Google/Kakao 로그인
    ├─ signup/            → 회원가입
    ├─ pwreset/           → 비밀번호 재설정
    ├─ home/              → 홈 대시보드, 방해금지모드, 일정 실행
    ├─ calendar/          → 캘린더 뷰
    ├─ aiplanner/         → AI 스케줄 생성/조회/수정
    ├─ mypage/            → 프로필, 계정 설정, 알림, 피드백
    └─ survey/            → 설문조사
```

  각 Feature 모듈은 
  Convention Plugin(convention.feature, convention.retrofit)을 통해
  빌드 설정을 표준화하고, core 모듈에만 의존합니다.

<br><br><br>

# 기여

## 역할 구분
- 방해금지모드
- 마이페이지
- 하단 네비게이션

<br><br><br>

# 구현 기능 상세

## 방해 금지 모드
**상태 흐름**
```
  타이머 SET → 타이머 RUNNING → 타이머 PAUSED → (Resume) → RUNNING
                         → (End)    → COMPLETED
                → (TimeFinished)    → COMPLETED
```

<br>
                
**데이터 흐름**

<img width="642" height="622" alt="Screenshot 2026-02-25 at 2 35 31 AM" src="https://github.com/user-attachments/assets/884550dc-cdbc-42c4-a433-21defc5c27e2" />

  ViewModel 공유: HomeNavigation에서 hiltViewModel(parentEntry)를 사용하여
  단일 ViewModel을 DndTimer/DndPause/DndEarlyEnd 화면 간 공유합니다.

<br>

## 마이페이지
**프로필 편집**
  - MyPageViewModel이 isEditMode 상태로 View/Edit 모드 전환
  - 프로필 이미지: 6개 아바타 이미지를 좌우 방향으로 선택
  (selectedProfileImageIndex를 순환)
  - 닉네임: TextField와 onValueChange로 실시간 입력, "완료" 클릭 시 서버 PATCH

<br>

**피드백**
  - FeedbackViewModel이 제목, 내용, 사진(최대 10장), 개인정보 동의를 State로
  관리
  - isSubmitEnabled는 제목 + 내용 + 개인정보 동의 여부의 파생 상태

<br><br><br>

# 기술적 문제 해결
  ## 1. 방해금지모드 온보딩이 반복 표시되는 문제
  증상: DataStore로 온보딩 완료 여부를 저장하는 로직을 구현했으나, 방해금지모드
  진입 시 매번 온보딩이 표시됨.

원인 분석
  1. HomeScreen 의 Effect 수집 when 절에서 Effect 처리 누락으로 인해 네비게이션 발생 x
  2. DndOnboardingScreen 클릭 이벤트 발생 시 navigation(route) 만 호출하고,
DndContract.Event 를 발행하지 않았다. DataStore 에 완료상태가 저장되지 않아, 매번 미완료로 판단.

해결
  1. HomeScreen에 ToDndOnboarding Effect 핸들러 추가
  2. DndOnboardingScreen에 onStartClick: () -> Unit 콜백 파라미터 추가
  3. HomeNavigation에서 콜백 내에서 viewModel.setEvent(CompleteOnboarding) +
  네비게이션을 함께 처리

<br>

  ## 2. 타이머 viewModel 공유
  증상 : 타이머 실행 중 일시정지 화면으로 이동 후 "이어하기" 클릭 시 타이머가 0으로 초기화

원인 분석
  1. 각 Composable에서 viewModel()을 호출하면 Navigation destination마다 새
  ViewModel 인스턴스가 생성된다. DndTimerScreen과 DndPauseScreen이 서로 다른
  ViewModel 인스턴스를 참조하고 있었다.

해결
  HomeNavigation에서
  navController.getBackStackEntry(HomeRoute.Home.route)로 Home의 백스택 엔트리를
  가져오고, hiltViewModel(parentEntry)로 동일한 스코프의 ViewModel을 공유한다.
  타이머, 일시정지, 조기종료, 완료 화면 모두 동일한 DndTimerViewModel 인스턴스를 참조한다.

<br><br><br>

# 서버 아키텍쳐 및 네트워크 레이어

## 백엔드 구조

| 프레임워크 | Java 21, Spring Boot 3.x |
| API 설계 | RESTful |
| ORM | Spring Data JPA + MySQL |
| 인증 | Spring Security + JWT (Access/Refresh Token)  |
| 캐싱| Redis |
| 파일 저장 | 24 / 36 |
| 배포 | Docker 컨테이너 |
| 모니터링| Actuator + Prometheus |

<br>

## 클라이언트 - 서버 통신
Retrofit 2.11.0 + OkHttp 4.12.0 기반이며,
모든 API 응답은 공통 wrapper 를 사용합니다.

```kotlin
  data class ApiResponse<T>(
      val errorCode: String?,
      val message: String?,
      val result: T?
  )
```

<br>

JWT 토큰 자동 주입
AuthInterceptor가 OkHttp Interceptor로 등록되어,
모든 요청에 token 헤더를 자동 추가하도록 설계했습니다. 
TokenProvider는 DataStore의 토큰을 메모리에 캐싱하여 
매 요청마다 디스크 I/O를 방지합니다.

<br>

## 에러 처리
```kotlin
  NetworkResult<T> sealed interface로 성공/실패를 분리한다:

  sealed interface NetworkResult<out T> {
      data class Success<T>(val data: T) : NetworkResult<T>
      data class Failure(val error: NetworkError) : NetworkResult<Nothing>
  }
```

executeApiRequest 함수가 모든 API 호출을 try-catch로 감싸며,
Throwable을 NetworkError로 변환합니다.

<br>

```kotlin
fun NetworkError.toDomainError(): DataError = when (this) {
    is NetworkError.Http -> DataError.CustomError(code = code.toString(), msg = message ?: "요청 처리 중 문제가 발생했어요.")
    NetworkError.Timeout -> DataError.CustomError(code = "TIMEOUT", msg = "응답이 늦어요. 잠시 후 다시 시도해 주세요.")
    NetworkError.NoConnection,
    NetworkError.ConnectionLost,
    NetworkError.DnsResolutionFailed,
    NetworkError.HostUnreachable -> DataError.CustomError(code = "NETWORK", msg = "인터넷 연결을 확인해 주세요.")
    NetworkError.SslHandshakeFailed -> DataError.CustomError(code = "SSL", msg = "보안 연결에 문제가 있어요.")
    NetworkError.ParseError -> DataError.CustomError(code = "PARSE", msg = "요청 처리 중 문제가 발생했어요.")
    is NetworkError.Unknown -> DataError.Unknown
}
```

ViewModel 레이어에서는 DataResult로 한번 더 래핑하여 UI에 전달하며,
showErrorMessage()로 사용자에게 적절한 메시지를 표시합니다.

<br><br><br>


📜 MIT License
