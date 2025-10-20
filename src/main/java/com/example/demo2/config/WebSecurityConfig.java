package com.example.demo2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider; // 必要に応じて
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // 必要に応じて
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// AccountUserDetailsServiceはそのまま使用（UserDetailsServiceを実装している前提）
import com.example.demo2.service.AccountUserDetailsService; 

@Configuration 
@EnableWebSecurity // WebSecurityConfigurerAdapterの継承は不要になりました
@EnableMethodSecurity(prePostEnabled = true) // @EnableGlobalMethodSecurity の新しい名前
public class WebSecurityConfig {

    // 以前のuserDetailsService@Autowired は Bean として定義することで代替

    /**
     * パスワードエンコーダーのBean定義
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 認証プロバイダーのBean定義（UserDetailsServiceとPasswordEncoderを関連付ける）
     */
    @Bean
    public AuthenticationProvider authenticationProvider(AccountUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // サービスを設定
        provider.setPasswordEncoder(passwordEncoder);       // エンコーダーを設定
        return provider;
    }

    /**
     * セキュリティフィルターチェーンのBean定義 (Http Securityの設定)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 認可の設定
            .exceptionHandling(handling -> handling
                .accessDeniedPage("/accessDeniedPage") // アクセス拒否された時に遷移するパス
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/loginForm").permitAll() // loginFormへのアクセスを許可
                .anyRequest().authenticated() // それ以外のリクエストは認証が必要
            )
            // ログイン設定
            .formLogin(form -> form
                .loginPage("/loginForm")         // ログインフォームを表示するパス
                .loginProcessingUrl("/authenticate") // フォーム認証処理のパス
                .usernameParameter("userName")   // ユーザ名のリクエストパラメータ名
                .passwordParameter("password")   // パスワードのリクエストパラメータ名
                .defaultSuccessUrl("/home")      // 認証成功時に遷移するデフォルトのパス
                .failureUrl("/loginForm?error=true") // 認証失敗時に遷移するパス
            )
            // ログアウト設定
            .logout(logout -> logout
                .logoutSuccessUrl("/loginForm")  // ログアウト成功時に遷移するパス
                .permitAll()                     // 全ユーザに対して許可
            );
        
        return http.build(); // SecurityFilterChainをビルドして返す
    }
}