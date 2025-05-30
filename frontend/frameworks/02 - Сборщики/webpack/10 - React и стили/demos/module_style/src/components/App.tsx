import * as styles from "./App.module.css";

export default function App() {
  return (
    <div className={`${styles.card} ${styles.cardSpace}`}>
      <h1 className={styles.header}>Hello, webpack!</h1>
      <p>Пример на использование модульных стилей.</p>
    </div>
  )
}