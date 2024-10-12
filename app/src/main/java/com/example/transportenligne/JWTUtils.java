package com.example.transportenligne;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.auth0.android.jwt.JWT;

import java.util.Arrays;
import java.util.Date;

public class JWTUtils {
    static SharedPreferences prefs;
    static SharedPreferences.Editor edit;
    public JWTUtils(Context context){
       setContext(context);
    }

    private static void setContext(Context context) {
        prefs = context.getSharedPreferences("TransportPFE",MODE_PRIVATE);
        edit = prefs.edit();
    }

    public static void SaveJWT(Context context,String token)
    {       setContext(context);

        edit.putString("token",token);
        edit.commit();
    }
    public static String GetJWT(Context context)
    {
        setContext(context);

        String token= prefs.getString("token","");
        return token;
    }
    public static String[] GetRoles(Context context){
        String[] roles;
        setContext(context);

        var token=GetJWT(context);
        JWT jwt = new JWT(token);
        roles=jwt.getClaim("roles").asArray(String.class);
        return roles;
    }
    public static boolean HasRole(Context context,String role)
    {       setContext(context);

        var roles =GetRoles(context);
        return Arrays.asList(roles).contains(role);
    }
    public static boolean IsClient(Context context)
    {  setContext(context);
        var roles =GetRoles(context);
        return Arrays.asList(roles).contains("ROLE_CLIENT");
    }
    public static boolean TokenExpired(Context context)
    {setContext(context);
        var token=GetJWT(context);
        JWT jwt=new JWT(token);
        Date expiresAt = jwt.getExpiresAt();
        var expirationdate= expiresAt.before(new Date());
        return expirationdate;
    }
    public static String GetValue(Context context,String key)
    {
        setContext(context);
        var token=GetJWT(context);
        JWT jwt = new JWT(token);
        var claims=jwt.getClaims();
       var value=jwt.getClaim(key).asString();
        return value;
    }
    public static String GetId(Context context)
    {setContext(context);
        return GetValue(context,"id");
    }

    public static void DeleteJWT(Context context) {
        setContext(context);
        edit.remove("token");edit.commit();
    }

    public static void SaveRefreshToken(Context context, String refresh_token) {
        setContext(context);

        edit.putString("refresh_token",refresh_token);
        edit.commit();
    }
    public static String GetRefreshToken(Context context)
    {
        setContext(context);
        String token= prefs.getString("refresh_token","");
        return token;
    }
    public static void Logout(Context context)
    {
        JWTUtils.DeleteJWT(context);
        Intent intent = new Intent(context, MainActivity_accueil.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }
}
