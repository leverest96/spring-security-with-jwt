<template>
    <div class="container is-max-desktop">
        <section class="section">
            <div class="box is-flex is-flex-direction-column py-6 has-background-light is-shadowless">
                <div class="block is-flex is-align-content-center is-justify-content-center">
                    <h1 class="title">인덱스 페이지</h1>
                </div>

                <div class="block is-flex is-align-content-center is-justify-content-center mt-5 mb-3">
                    <h4 class="subtitle is-4">회원 기능</h4>
                </div>

                <div class="block is-flex is-align-content-center is-justify-content-center">
                    <div class="buttons are-medium">
                        <a href="/register" class="button has-text-white has-background-grey">회원가입</a>
                        <a href="/login" class="button has-text-white has-background-grey">로그인</a>
                        <button class="button has-text-white has-background-grey" onclick="logout()">로그아웃</button>
                    </div>
                </div>

                <div class="block is-flex is-align-content-center is-justify-content-center mt-5 mb-3">
                    <h4 class="subtitle is-4">페이지 기능</h4>
                </div>

                <div class="block is-flex is-align-content-center is-justify-content-center">
                    <div class="buttons are-medium">
                        <a href="/member" class="button has-text-white has-background-grey-dark">사용자 페이지</a>
                        <a href="/admin" class="button has-text-white has-background-grey-dark">관리자 페이지</a>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
export default {
    
}
</script>