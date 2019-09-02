package dz.saticom.syvcom.dashboard.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        
		registry.addViewController("/dashboard").setViewName("dashboard");
		registry.addViewController("/dashboard2").setViewName("dashboard2");
        registry.addViewController("/tables").setViewName("tables");
        registry.addViewController("/icons").setViewName("icons");
        registry.addViewController("/map").setViewName("map");
        registry.addViewController("/notifications").setViewName("notifications");
        registry.addViewController("/rtl").setViewName("rtl");
        registry.addViewController("/typography").setViewName("typography");
        registry.addViewController("/upgrade").setViewName("upgrade");
        registry.addViewController("/user").setViewName("user");
        
    }
}
