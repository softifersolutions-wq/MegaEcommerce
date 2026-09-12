package com.electromart.ecommerce.config;

import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.entity.UserEntity;
import com.electromart.ecommerce.repository.ProductRepository;
import com.electromart.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;



@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedProducts();
    }

    private void seedAdmin() {
        String adminEmail = "admin@electromart.com";
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        UserEntity admin = new UserEntity();
        admin.setName("Store Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        List<ProductEntity> products = List.of(
            product("Nimbus Pro 15 Laptop", "Laptops", "Nimbus", 1299, 1499.0, 4.6, 214,
                "https://picsum.photos/seed/nimbus-laptop/600/600",
                "A powerful 15\" laptop with a crisp OLED display, all-day battery life, and a lightning-fast processor built for creators and professionals.",
                List.of("15.6\" 2.8K OLED display", "16GB RAM / 1TB SSD", "12-core processor", "Up to 18 hours battery"),
                24, "Best Seller"),

            product("Aura X1 Smartphone", "Smartphones", "Aura", 899, 999.0, 4.7, 512,
                "https://picsum.photos/seed/aura-phone/600/600",
                "Flagship smartphone with a triple-lens camera system, edge-to-edge display, and blazing 5G connectivity.",
                List.of("6.7\" AMOLED 120Hz display", "256GB storage", "Triple 50MP camera system", "5000mAh battery"),
                40, "New"),

            product("SoundWave Pro Headphones", "Audio", "SoundWave", 249, null, 4.5, 388,
                "https://picsum.photos/seed/soundwave-headphones/600/600",
                "Over-ear wireless headphones with adaptive noise cancellation and studio-grade sound tuning.",
                List.of("Active noise cancellation", "40-hour battery life", "Bluetooth 5.3", "Memory-foam ear cushions"),
                65, null),

            product("PulseFit Watch Series 4", "Wearables", "PulseFit", 199, 249.0, 4.3, 176,
                "https://picsum.photos/seed/pulsefit-watch/600/600",
                "Track your health around the clock with heart-rate, SpO2, and sleep monitoring in a sleek aluminum case.",
                List.of("1.9\" AMOLED display", "Heart rate & SpO2 sensors", "7-day battery", "5ATM water resistance"),
                52, "Sale"),

            product("ClearShot Z9 Mirrorless Camera", "Cameras", "ClearShot", 1099, null, 4.8, 92,
                "https://picsum.photos/seed/clearshot-camera/600/600",
                "Full-frame mirrorless camera with 45MP sensor and in-body stabilization for pro-level stills and video.",
                List.of("45MP full-frame sensor", "8K video recording", "In-body 5-axis stabilization", "Weather-sealed body"),
                15, null),

            product("TitanBook Air 13", "Laptops", "Titan", 999, null, 4.4, 301,
                "https://picsum.photos/seed/titanbook-air/600/600",
                "Ultra-thin and light 13\" laptop, perfect for students and everyday productivity on the go.",
                List.of("13.3\" IPS display", "8GB RAM / 512GB SSD", "Fanless silent design", "1.1kg lightweight body"),
                38, null),

            product("Nova Buds Wireless Earbuds", "Audio", "Nova", 129, 159.0, 4.2, 445,
                "https://picsum.photos/seed/nova-buds/600/600",
                "True wireless earbuds with punchy bass, transparency mode, and a compact wireless-charging case.",
                List.of("Active noise cancellation", "6h + 24h case battery", "IPX5 sweat resistant", "Touch controls"),
                88, "Sale"),

            product("Vortex GX Gaming Console", "Gaming", "Vortex", 549, null, 4.9, 267,
                "https://picsum.photos/seed/vortex-console/600/600",
                "Next-gen gaming console delivering 4K/120fps gameplay with near-instant load times.",
                List.of("4K HDR @ 120fps", "1TB NVMe SSD", "Ray tracing support", "Backward compatible library"),
                18, "Best Seller"),

            product("CrystalView 55\" 4K TV", "TVs", "CrystalView", 649, 799.0, 4.5, 158,
                "https://picsum.photos/seed/crystalview-tv/600/600",
                "Vivid 55-inch 4K QLED smart TV with immersive Dolby Atmos sound and smooth motion technology.",
                List.of("55\" 4K QLED panel", "Dolby Vision & Atmos", "120Hz motion smoothing", "Built-in smart hub"),
                22, "Sale"),

            product("FlexPad 11 Tablet", "Tablets", "FlexPad", 429, null, 4.4, 133,
                "https://picsum.photos/seed/flexpad-tablet/600/600",
                "Slim 11\" tablet with stylus support, ideal for note-taking, sketching, and streaming.",
                List.of("11\" Liquid Retina display", "128GB storage", "Stylus pen supported", "10-hour battery life"),
                47, null),

            product("EchoDot Smart Speaker", "Smart Home", "Echo", 59, null, 4.1, 620,
                "https://picsum.photos/seed/echodot-speaker/600/600",
                "Compact smart speaker with voice assistant, rich sound, and smart-home control built in.",
                List.of("360\u00b0 immersive sound", "Built-in voice assistant", "Multi-room audio", "Smart home hub"),
                120, null),

            product("RapidCharge 65W GaN Charger", "Accessories", "RapidCharge", 39, null, 4.6, 289,
                "https://picsum.photos/seed/rapidcharge-charger/600/600",
                "Compact GaN fast charger that powers laptops, phones, and tablets from a single tiny brick.",
                List.of("65W USB-C PD output", "Dual-port charging", "GaN compact design", "Universal compatibility"),
                200, null),

            product("DriftCam Action Camera", "Cameras", "Drift", 279, null, 4.3, 97,
                "https://picsum.photos/seed/driftcam-action/600/600",
                "Rugged waterproof action camera with 5.3K recording and built-in hyper-stabilization.",
                List.of("5.3K/30fps video", "Waterproof to 10m", "HyperSmooth stabilization", "Voice control"),
                33, "New"),

            product("KeyForge Mechanical Keyboard", "Accessories", "KeyForge", 119, 149.0, 4.7, 210,
                "https://picsum.photos/seed/keyforge-keyboard/600/600",
                "Hot-swappable mechanical keyboard with per-key RGB and a satisfying tactile switch feel.",
                List.of("Hot-swappable switches", "Per-key RGB lighting", "Aluminum top plate", "Wired + wireless modes"),
                58, "Sale"),

            product("OrbitCharge Wireless Pad", "Accessories", "Orbit", 34, null, 4.0, 154,
                "https://picsum.photos/seed/orbitcharge-pad/600/600",
                "Sleek 15W wireless charging pad with a soft-glow LED ring and non-slip base.",
                List.of("15W fast wireless charging", "LED charge indicator", "Non-slip silicone base", "Case-friendly design"),
                140, null),

            product("SkyView Drone 4K", "Gaming", "SkyView", 799, 899.0, 4.6, 76,
                "https://picsum.photos/seed/skyview-drone/600/600",
                "Foldable 4K camera drone with obstacle avoidance and 34 minutes of flight time per charge.",
                List.of("4K/60fps gimbal camera", "34-min flight time", "Obstacle avoidance sensors", "Foldable compact frame"),
                12, "Limited")
        );

        productRepository.saveAll(products);
    }

    private ProductEntity product(String name, String category, String brand, double price, Double oldPrice,
                                   double rating, int reviews, String image, String description,
                                   List<String> specs, int stock, String tag) {
        ProductEntity p = new ProductEntity();
        p.setName(name);
        p.setCategory(category);
        p.setBrand(brand);
        p.setPrice(price);
        p.setOldPrice(oldPrice);
        p.setRating(rating);
        p.setReviews(reviews);
        p.setImage(image);
        p.setDescription(description);
        p.setSpecs(specs);
        p.setStock(stock);
        p.setTag(tag);
        return p;
    }
}
