# UI-тесты интернет-магазина Saucedemo

Домашние задания №4–7: UI-тесты на Playwright for Java + JUnit 5 с паттернами Page Object, Page Element, Loadable Component и другими.
Проект лежит в папке [`ui-tests/`](ui-tests).

Объект тестирования — демо-магазин [saucedemo.com](https://www.saucedemo.com).
Тестовые учётные данные публичные, они указаны на странице входа.

## Запуск

Требуются JDK 21+ и Maven. При первом запуске Playwright сам скачает браузер Chromium.

```bash
cd ui-tests
mvn test                     # все тесты
mvn test -Dtest=LoginTest    # один класс
mvn test -Dgroups=smoke      # по тегу
mvn test -Dtest=SmokeSuite   # набор: smoke-тесты из пакета tests
```

## Структура

```
ui-tests/src/test/java/com/github/mishakatan/
├── data/
│   └── User.java            тестовые пользователи (Value Object)
├── elements/
│   └── ProductCard.java     карточка товара (Page Element)
├── pages/
│   ├── BasePage.java        общий предок страниц (Loadable Component)
│   ├── LoginPage.java       страница входа
│   └── InventoryPage.java   каталог товаров
└── tests/
    ├── BaseTest.java        запуск браузера, свой контекст на каждый тест
    ├── LoginTest.java       тесты входа
    ├── InventoryTest.java   тесты каталога и корзины
    └── SmokeSuite.java      набор smoke-тестов
```

Тесты работают только через методы Page Object: локаторы и объект `Page` закрыты внутри страниц и `BaseTest`.
Проверки — через `assertThat` из Playwright с автоожиданием, без `Thread.sleep`.

## Паттерны

| Паттерн | Где используется |
|---|---|
| Page Object | `LoginPage`, `InventoryPage`: локаторы и действия страницы закрыты внутри класса |
| Page Element | `ProductCard`: карточка товара со своими локаторами, ищутся внутри карточки |
| Loadable Component | `BasePage.waitUntilLoaded()`: страница проверяет, что загрузилась, до работы с ней; `LoginPage.open()` и `loginAs()` возвращают уже загруженную страницу |
| Chain of invocations | методы страниц возвращают следующую страницу: `openLoginPage().loginAs(user).addToCart(...).shouldHaveCartItemsCount(1)` |
| Value Object | `record User`: логин и пароль одним неизменяемым объектом, константы `User.STANDARD`, `User.LOCKED_OUT` и др. |
| Data Provider | `LoginTest.usersWhoCanLogIn()`: поставщик пользователей для `@MethodSource` |

## Правила локаторов

1. **Один тестовый атрибут на проект** — `data-test`. Он задан один раз в `BaseTest`
   (`setTestIdAttribute("data-test")`), элементы ищутся через `getByTestId`.
2. **Не зависим от языка интерфейса** — не ищем по надписям кнопок и полей
   (`"Login"`, `"Add to cart"`): при переводе или смене текста локатор не сломается.
3. **Нет CSS-классов, XPath и индексов** (`nth-child`, `first()`): они ломаются при изменении вёрстки.
4. **Динамический атрибут — по префиксу.** У кнопки добавления в корзину атрибут свой у каждого товара
   (`add-to-cart-sauce-labs-backpack`), поэтому она ищется как `[data-test^='add-to-cart']` внутри карточки.
5. **Поиск по смыслу внутри родителя.** Карточка товара выбирается по элементу названия
   (`inventory-item-name`), а её кнопка ищется только внутри этой карточки.
   Имя товара здесь — входные данные теста, а не надпись интерфейса.
6. **Один дом для локатора** — `private final` поля Page Object и Page Element, тесты локаторов не видят.
7. **Локаторы ленивые.** `Locator` в Playwright ищет элемент заново при каждом действии и сам ждёт его появления,
   поэтому нет устаревших ссылок на узлы DOM и `Thread.sleep`.

| Элемент | Локатор |
|---|---|
| Поля входа, кнопка входа, ошибка | `getByTestId("username")`, `"password"`, `"login-button"`, `"error"` |
| Заголовок, список, карточки, значок корзины | `getByTestId("title")`, `"inventory-list"`, `"inventory-item"`, `"shopping-cart-badge"` |
| Карточка конкретного товара | `inventory-item`, внутри которой `inventory-item-name` с именем товара |
| Кнопка «в корзину» | `[data-test^='add-to-cart']` внутри карточки |

## Аннотации JUnit 5

| Аннотация | Где используется |
|---|---|
| `@Test` | все обычные тесты |
| `@BeforeAll` / `@AfterAll` | `BaseTest`: запуск и закрытие браузера |
| `@BeforeEach` / `@AfterEach` | `BaseTest`: новый контекст на тест; `InventoryTest`: вход перед тестом |
| `@DisplayName` | названия классов и тестов в отчёте |
| `@Tag` | `smoke`, `negative` |
| `@Disabled` | тест на известный баг `problem_user` |
| `@Timeout` | `LoginTest`: не дольше 30 секунд на тест |
| `assertAll` | `InventoryTest.catalogShowsAllProducts` |
| `@ParameterizedTest` | `LoginTest.userCanLogIn`: три пользователя из `@MethodSource` |
| `@Nested` | `InventoryTest.Cart`: группа тестов корзины |
| `@TestFactory` | `InventoryTest.Cart.cartCounterGrows`: динамические тесты по списку товаров |
| `@Suite` | `SmokeSuite`: запуск по пакету и тегу |

## Что проверяется

| Тест | Проверка |
|---|---|
| `LoginTest.userCanLogIn` | вход под `standard_user`, `problem_user`, `visual_user` открывает каталог |
| `LoginTest.lockedOutUserSeesError` | вход под `locked_out_user` показывает ошибку о блокировке |
| `InventoryTest.catalogShowsAllProducts` | каталог открыт, в нём 6 товаров |
| `InventoryTest.Cart.addedProductIsShownInCartBadge` | после добавления товара на значке корзины `1` |
| `InventoryTest.Cart.cartCounterGrows` | счётчик корзины растёт: 1, 2, 3 |
| `InventoryTest.Cart.problemUserCanAddBoltTShirt` | отключён: `problem_user` не может добавить Bolt T-Shirt (баг сайта) |

# Автотесты REST API авторизации DummyJSON

Домашнее задание: тесты REST API на Playwright + TypeScript (Взял вместо movix, чтобы никакие секреты не утекли, но с таким же стеком для тренировки).

Объект тестирования — публичное API авторизации [DummyJSON](https://dummyjson.com/docs/auth):
`POST /auth/login`, `GET /auth/me`, `POST /auth/refresh`.

## Запуск

Требуется Node.js 18+.

```bash
npm install          # установить зависимости
npm test             # запустить все тесты
npm run typecheck    # проверка типов TypeScript
npm run report       # открыть HTML-отчёт последнего прогона
```

Адрес API по умолчанию — `https://dummyjson.com`, его можно переопределить:

```bash
API_BASE_URL=https://другой-стенд npm test
```

## Структура

```
docs/test-cases.md   тест-кейсы и найденные баги
tests/               тесты, по файлу на эндпоинт
clients/             AuthClient — методы login, me, refresh; тесты не знают про URL и HTTP
data/                тестовый пользователь и константы
utils/               getTokens — получение токенов; tokenLifetimeMins — срок жизни JWT
```

## Что проверяется

Подробные шаги и ожидаемые результаты — в [docs/test-cases.md](docs/test-cases.md).

| Кейс | Эндпоинт | Проверка | Тип |
|---|---|---|---|
| TC-1 | login | вход с верными данными: токены JWT, данные пользователя, нет пароля, токен на 60 мин | позитивный |
| TC-2 | login | неверный пароль → 400 | негативный |
| TC-3 | login | несуществующий логин → 400, то же сообщение, что в TC-2 | негативный |
| TC-4 | login | пустое тело, нет логина/пароля, пустые строки → 400 (6 вариантов) | валидация |
| TC-5 | login | `expiresInMins`: 1 и 43200 — токен на указанный срок; 43201, 0, -5, `"abc"` → 400 | границы, валидация |
| TC-6 | me | профиль по access-токену; пароль не возвращается | позитивный |
| TC-7 | me | мусорный токен, токен неверного формата, испорченная подпись, refresh вместо access → 401 | негативный |
| TC-8 | me | без токена → 401 | негативный |
| TC-9 | refresh | новая пара токенов, новый access-токен работает | позитивный |
| TC-10 | refresh | токен не передан → 401; мусорный токен, access вместо refresh → 403 | негативный |

Всего 26 тестов: часть кейсов параметризована.

## Найденные баги

Тесты на баги проверяют **ожидаемое** поведение и помечены `test.fail()` со ссылкой на баг.
Прогон остаётся зелёным; когда баг исправят, тест сообщит «expected to fail, but passed» —
это сигнал снять пометку. Подробные описания — в [docs/test-cases.md](docs/test-cases.md#найденные-баги).

| Баг | Суть | Серьёзность |
|---|---|---|
| BUG-1 | `expiresInMins` = 0 → 200 и токен на 30 дней вместо ошибки | Major |
| BUG-2 | `expiresInMins` = 43201 → 500 вместо 400 | Minor |
| BUG-3 | `expiresInMins` = -5 → 500 и сообщение про максимум | Minor |
| BUG-4 | `expiresInMins` = `"abc"` → 500 и сообщение про максимум | Minor |
| BUG-5 | `GET /auth/me` возвращает пароль открытым текстом | Critical |
| BUG-6 | токен неверного формата → 500 вместо 401 | Minor |
| BUG-7 | токен с испорченной подписью → 500 вместо 401 | Minor |
| BUG-8 | refresh-токен принимается в `/auth/me` вместо access | Major |
| BUG-9 | access-токен принимается в `/auth/refresh` вместо refresh | Major |

## Допущения

- **Общий тестовый пользователь.** DummyJSON не позволяет по-настоящему создать пользователя,
  поэтому все тесты используют демо-пользователя `emilys` из документации. Тесты его не изменяют,
  только читают, поэтому параллельный запуск безопасен.
- **Пароль в коде.** Учётные данные публичные и одинаковы для всех. В реальном проекте
  они передавались бы через переменные окружения или секреты CI.
- **Cookie.** При входе DummyJSON ставит cookie `accessToken`, и `/auth/me` её принимает.
  Поэтому токены для подготовки тестов получаются в отдельном, сразу закрываемом контексте
  (`utils/auth.ts`), а проверки идут только через заголовок `Authorization`.
