# DevLink 프로젝트

<img width="988" height="785" alt="스크린샷 2025-08-20 오전 12 21 18" src="https://github.com/user-attachments/assets/6043c0a1-901c-4e6d-b4c3-f58dc646b2b0" />



이 프로젝트는 `GitHub`, `Stack Overflow`와 같은 개발자 커뮤니티 플랫폼을 참고하여 개발된 서비스로

개발 지식 공유를 위한 아티클 작성 및 실시간 피드 기반 소통 기능을 중점적으로 다루고 있습니다.


<br/>


## 🔍 Tech Stack

`Java 17`, `Spring boot 3.5.3`, `Spring Data JPA`, `MySQL`, `Redis`, `AWS EC2`, `Gradle`, `JUnit`


<br/>


## 🚀 프로젝트 목표

- 조회수/랭킹 기능을 구현하면서 `“실시간 vs 배치 처리”`의 경계를 고민했고, 실시간성과 지연 반영 사이에서의 균형을 찾고자 했습니다.

- 트래픽이 같은 게시글에 집중될 때 DB에 부하를 주지 않고 조회수를 관리하기 위해, `Redis 캐싱`과 `배치 업데이트` 방식을 도입하며 시스템 구현을 했습니다.

- 인기글 Top 5 계산에서 매번 전체 데이터를 정렬하는 비효율을 개선하고자, 스케줄러 기반 캐싱으로 `O(1) 조회가 가능한 구조`를 설계했습니다.



<br/>

## 💭 프로젝트를 진행하며 했던 고민

- [1. MySQL 부하를 줄이는 실시간 조회수 업데이트 개선](https://mingseok-blog.vercel.app/blog/TIL/2025-02/MySQL_%EB%B6%80%ED%95%98%EB%A5%BC_%EC%A4%84%EC%9D%B4%EB%8A%94_%EC%8B%A4%EC%8B%9C%EA%B0%84_%EC%A1%B0%ED%9A%8C%EC%88%98_%EC%97%85%EB%8D%B0%EC%9D%B4%ED%8A%B8_%EA%B0%9C%EC%84%A0)

- [2. 인기글 조회를 위한 MySQL 정렬 쿼리 개선](https://mingseok-blog.vercel.app/blog/TIL/2025-02/%EC%9D%B8%EA%B8%B0%EA%B8%80_%EC%A1%B0%ED%9A%8C%EB%A5%BC_%EC%9C%84%ED%95%9C_MySQL_%EC%A0%95%EB%A0%AC_%EC%BF%BC%EB%A6%AC_%EA%B0%9C%EC%84%A0)


<br/>

## 🎯 기능 시연 영상

https://github.com/user-attachments/assets/9dceb731-c221-4d3a-96ac-0dfff541e264

- [전체 기능 시연 영상](https://www.youtube.com/watch?v=zrC-8EakGgA&t=1s)


<br/>


## 📌 브랜치 전략 (Git-Flow)

✅ `master` : 배포시 사용할 브랜치. -> (완벽히 제품으로 출시될 수 있는 브랜치를 의미합니다)

✅ `develop` : 다음 버전을 개발하는 브랜치. -> (feature에서 리뷰 완료한 브랜치를 Merge 하고 합니다.)

✅ `feature` : 기능을 개발하는 브랜치

✅ `release` : 배포를 준비할 때 사용할 브랜치

✅ `hotfix` : 배포 후에 발생한 버그를 수정 하는 브랜치


<br/>


## 💡 실시간 인기글 랭킹 시스템 성능 개선 결과


<table>
  <tr>
    <td width="50%" align="center">
      <a href="https://github.com/user-attachments/assets/9c237f37-931b-4428-b25e-e33fdffb401e">
        <img alt="screenshot-2" src="https://github.com/user-attachments/assets/9c237f37-931b-4428-b25e-e33fdffb401e" width="100%">
      </a>
      <sub>조회수 기능의 성능이 개선</sub>
    </td>
    <td width="50%" align="center">
      <a href="https://github.com/user-attachments/assets/b478db5f-7465-4103-bc46-bb7c560d4919">
        <img alt="screenshot-1" src="https://github.com/user-attachments/assets/b478db5f-7465-4103-bc46-bb7c560d4919" width="100%">
      </a>
      <sub>응답 시간 80% 이상 개선</sub>
    </td>
  </tr>
</table>



<br/>

## ✏️ Redis 실시간 인기글 랭킹 시스템

<img width="1276" height="1485" alt="image" src="https://github.com/user-attachments/assets/350cf836-da81-4510-a524-71d81265f4e9" />


<br/>

<br/>

## 📚 조회수 기능 Flow

<img width="5923" height="2891" alt="image" src="https://github.com/user-attachments/assets/56cd2584-77e4-4d48-818c-148c914ceeea" />

<br/> <br/>

## 📚 인기글 기능 Flow

<img width="6629" height="2890" alt="image" src="https://github.com/user-attachments/assets/0405a0da-de3f-44d3-aa07-8c6d324b77e7" />



<br/>

<br/>

## 🔖 ER 다이어그램

<img width="753" height="566" alt="스크린샷 2025-08-20 오전 2 23 40" src="https://github.com/user-attachments/assets/88ea856a-15f6-4c6d-865d-45cd39eb5000" />








