# Инструкция по установке Custom Crosshair Mod

## Для обычных игроков (установка готового мода)

### Шаг 1: Установка Forge
1. Скачайте Minecraft Forge 1.20.1 с официального сайта: https://files.minecraftforge.net/
2. Выберите версию 47.3.0 или новее
3. Запустите установщик и выберите "Install client"
4. Дождитесь завершения установки

### Шаг 2: Установка мода
1. Скачайте файл мода customcrosshair-1.0.0.jar
2. Откройте папку .minecraft:
   - Windows: нажмите Win+R, введите `%appdata%\.minecraft` и нажмите Enter
   - macOS: `~/Library/Application Support/minecraft`
   - Linux: `~/.minecraft`
3. Перейдите в папку `mods` (создайте её, если отсутствует)
4. Скопируйте файл customcrosshair-1.0.0.jar в папку mods
5. Запустите Minecraft Launcher
6. Выберите профиль с Forge 1.20.1
7. Запустите игру

### Шаг 3: Использование
1. В игре нажмите клавишу **H** для открытия меню настроек
2. Настройте прицел по своему вкусу или выберите готовый пресет
3. Нажмите "Готово" для сохранения

---

## Для разработчиков (сборка из исходников)

### Требования:
- JDK 17
- Git (опционально)

### Структура проекта:
Создайте следующую структуру папок:

```
CustomCrosshairMod/
├── gradle/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── customcrosshair/
│       │           ├── CustomCrosshairMod.java
│       │           ├── KeyBindings.java
│       │           ├── CrosshairConfig.java
│       │           ├── CrosshairRenderer.java
│       │           ├── CrosshairConfigScreen.java
│       │           └── ClientEventHandler.java
│       └── resources/
│           ├── META-INF/
│           │   └── mods.toml
│           └── assets/
│               └── customcrosshair/
│                   └── lang/
│                       ├── en_us.json
│                       └── ru_ru.json
├── build.gradle
├── settings.gradle
├── gradle.properties
└── gradlew (и gradlew.bat для Windows)
```

### Установка файлов:
1. Скопируйте все .java файлы в `src/main/java/com/customcrosshair/`
2. Скопируйте mods.toml в `src/main/resources/META-INF/`
3. Скопируйте en_us.json и ru_ru.json в `src/main/resources/assets/customcrosshair/lang/`
4. Разместите build.gradle, settings.gradle и gradle.properties в корневой папке

### Скачивание Gradle Wrapper:
Если у вас нет gradlew, выполните:
```bash
gradle wrapper
```

### Сборка:
```bash
# Linux/macOS
./gradlew build

# Windows
gradlew.bat build
```

Готовый мод будет в `build/libs/customcrosshair-1.0.0.jar`

### Запуск для тестирования:
```bash
# Linux/macOS
./gradlew runClient

# Windows
gradlew.bat runClient
```

---

## Решение проблем

### Мод не загружается:
- Проверьте версию Forge (должна быть 47.3.0+)
- Проверьте версию Minecraft (должна быть 1.20.1)
- Убедитесь, что файл мода находится в папке mods

### Клавиша H не работает:
- Откройте настройки Minecraft → Управление
- Найдите категорию "Custom Crosshair"
- Переназначьте клавишу при необходимости

### Прицел не отображается:
- Убедитесь, что GUI не скрыт (клавиша F1)
- Проверьте, что вы в режиме от первого лица (F5)

### Настройки не сохраняются:
- Проверьте права доступа к папке .minecraft/config
- Удалите файл customcrosshair.json и перезапустите игру

---

## Удаление мода

1. Закройте Minecraft
2. Перейдите в папку .minecraft/mods
3. Удалите файл customcrosshair-1.0.0.jar
4. Опционально: удалите файл .minecraft/config/customcrosshair.json
