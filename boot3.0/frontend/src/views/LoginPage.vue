<template>
    <div class="container is-max-desktop">
        <section class="section">
            <div class="box is-flex is-flex-direction-column py-6 has-background-white-bis is-shadowless">
            <div class="block is-flex is-align-content-center is-justify-content-center">
                <h1 class="title">로그인 페이지</h1>
            </div>

            <div class="field mt-5 px-6">
                <p class="control has-icons-left">
                <input type="email" placeholder="이메일" id="email-input" class="input">

                <span class="icon is-small is-left">
                                <i class="fa-solid fa-envelope"></i>
                            </span>
                </p>
            </div>

            <div class="field mt-4 mb-5 px-6">
                <p class="control has-icons-left">
                <input type="password" placeholder="비밀번호" id="password-input" class="input">

                <span class="icon is-small is-left">
                                <i class="fa-solid fa-lock"></i>
                            </span>
                </p>
            </div>

            <div class="block is-flex is-align-content-center is-justify-content-center">
                <div class="buttons">
                <button class="button is-info" onclick="login()">로그인</button>
                <a href="/" class="button">돌아가기</a>
                </div>
            </div>
            </div>
        </section>
    </div>
</template>