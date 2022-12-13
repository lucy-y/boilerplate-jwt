### jwt token
- HS512 알고리즘을 이용하기 때문에 Secret Key 64Byte 이상필요

```echo '64Byte이상문자열' | base64```

### jwt - TokenProvider

## postman
### token 저장
- api/authoriztion 호출 후 set token 
  - Tests
    ```aidl
    var jsonData = JSON.parse(responseBody)
    pm.globals.set("jwt_token", jsonData.token);
    ```
  - token 변수에 저장 후 다른 API 호출 시
    Authorization > Bearer Token > {{jwt_token}}