import { useAuth } from "../context/AuthContext";
import { Navigate } from "react-router-dom";

export default function ProfilePage() {
  const { user } = useAuth();

  if (!user) return <Navigate to="/login" />;

  return (
    <div className="content">
      <h2>Личный кабинет</h2>
      <p>Добро пожаловать, {user.username}!</p>
    </div>
  );
}
