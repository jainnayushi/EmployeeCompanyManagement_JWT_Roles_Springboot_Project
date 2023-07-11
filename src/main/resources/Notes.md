/authenticate
/genToken
/api with token in header

header.payload.signature


main
User
UserDTO - to fetch data from postman
UserRepository
UserService extends UserDetailsService : Spring application security
UserServiceImpl : overrides loadUserByUsername : find by username and return password and username, roles([])

SecurityConfig : Starting Point
- Encode password BCryptPasEncoder bean
- DaoAuthProvider
- AuthManager
- SecurityFilterChain : Which all calls authenticated by ss + addFilterBefore - jwtFilter : /api - read token find credentials - match with db creds

JwtGeneratorValidator
- generateToken(username) - createToken(claims, subject) (like jwt.io) claims = type, algo info... subject = username



UserController
/registration : normal save to db
/genToken :  take usename and password as userDTO obj
- authManager.authenticate : ensures username and password and db one matches
- jwtGenVal.generateToken : generate token


jwtFilter extends OncePerRequestFilter: token creds and db creds are same
- get token from header
- after bearer get token
- find username from token
- token is not exppired
- validate token


welcome - generateToken - 




