<template>
    <div class="container is-max-desktop">
        <section class="section">
            <div class="box is-flex is-flex-direction-column py-6 has-background-white-bis is-shadowless">
            <div class="block is-flex is-align-content-center is-justify-content-center">
                <h1 class="title">회원가입 페이지</h1>
            </div>

            <div class="field has-addons mt-5 px-6">
                <p class="control has-icons-left is-expanded">
                <input type="email" placeholder="이메일" id="email-input" class="input">

                <span class="icon is-small is-left">
                                <i class="fa-solid fa-envelope"></i>
                            </span>
                </p>

                <p class="control">
                <button class="button" onclick="checkEmailDuplication()">중복 확인</button>
                </p>
            </div>

            <div class="field mt-4 px-6">
                <p class="control has-icons-left">
                <input type="password" placeholder="비밀번호" id="password-input" class="input">

                <span class="icon is-small is-left">
                                <i class="fa-solid fa-lock"></i>
                            </span>
                </p>
            </div>

            <div class="field mt-4 px-6">
                <p class="control has-icons-left">
                <input type="text" placeholder="닉네임" id="nickname-input" class="input">

                <span class="icon is-small is-left">
                                <i class="fa-solid fa-user"></i>
                            </span>
                </p>
            </div>

            <div class="block is-flex is-align-content-center is-justify-content-center mt-4">
                <label class="checkbox">
                <input type="checkbox" id="admin-checkbox"> 관리자
                </label>
            </div>

            <div class="block is-flex is-align-content-center is-justify-content-center">
                <div class="buttons">
                <button class="button is-info" onclick="register()">회원가입</button>
                <a href="/" class="button">돌아가기</a>
                </div>
            </div>
            </div>
        </section>
    </div>
</template>