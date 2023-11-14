import { BrowserRouter, Route, Routes } from "react-router-dom";
import IndexPage from "../pages/IndexPage";
import LoginPage from "../pages/LoginPage";
import MemberPage from "../pages/MemberPage";
import RegisterPage from "../pages/RegisterPage";

export const AppRouter = () => {
  return(
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={ <IndexPage/> }/>
          <Route path="/member" element={ <MemberPage/> }/>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={ <RegisterPage/> }/>
        </Routes>
      </BrowserRouter>
    </>
  )
}