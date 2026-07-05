#!/usr/bin/env python3
"""生成 LIVE星球 APP 图标和启动页资源"""

import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

OUTPUT_DIR = "/workspace/LiveStreamPlatform/live-user-app/src/static"
ICON_DIR = os.path.join(OUTPUT_DIR, "icon")
SPLASH_DIR = os.path.join(OUTPUT_DIR, "splash")

os.makedirs(ICON_DIR, exist_ok=True)
os.makedirs(SPLASH_DIR, exist_ok=True)

PRIMARY_COLORS = [(255, 45, 85), (175, 82, 237), (0, 212, 255)]
BG_DARK = (10, 10, 15)


def make_gradient(size, colors, direction='diagonal'):
    """创建渐变背景"""
    img = Image.new('RGBA', size, (0, 0, 0, 255))
    draw = ImageDraw.Draw(img)
    w, h = size
    
    if direction == 'diagonal':
        for y in range(h):
            for x in range(w):
                t = (x + y) / (w + h)
                r = int(colors[0][0] * (1-t) + colors[1][0] * t)
                g = int(colors[0][1] * (1-t) + colors[1][1] * t)
                b = int(colors[0][2] * (1-t) + colors[1][2] * t)
                draw.point((x, y), fill=(r, g, b, 255))
    elif direction == 'vertical':
        for y in range(h):
            t = y / h
            r = int(colors[0][0] * (1-t) + colors[1][0] * t)
            g = int(colors[0][1] * (1-t) + colors[1][1] * t)
            b = int(colors[0][2] * (1-t) + colors[1][2] * t)
            draw.line([(0, y), (w, y)], fill=(r, g, b, 255))
    
    return img


def draw_play_icon(draw, cx, cy, size, color):
    """绘制播放三角形图标"""
    s = size
    points = [
        (cx - s * 0.3, cy - s * 0.4),
        (cx + s * 0.5, cy),
        (cx - s * 0.3, cy + s * 0.4),
    ]
    draw.polygon(points, fill=color)


def draw_circle_outline(draw, cx, cy, radius, color, width=3):
    """绘制圆形轮廓"""
    draw.ellipse(
        [cx - radius, cy - radius, cx + radius, cy + radius],
        outline=color, width=width
    )


def generate_icon(size):
    """生成 APP 图标"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 255))
    draw = ImageDraw.Draw(img)
    
    # 背景渐变
    for y in range(size):
        t = y / size
        r = int(30 * (1-t) + 20 * t)
        g = int(15 * (1-t) + 10 * t)
        b = int(40 * (1-t) + 30 * t)
        draw.line([(0, y), (size, y)], fill=(r, g, b, 255))
    
    # 外圈光晕
    glow_radius = size * 0.42
    for i in range(30):
        alpha = int(60 * (1 - i/30))
        r = int(255 * (1 - i/30) + 175 * (i/30))
        g = int(45 * (1 - i/30) + 82 * (i/30))
        b = int(85 * (1 - i/30) + 237 * (i/30))
        radius = glow_radius - i * 0.5
        draw.ellipse(
            [size//2 - radius, size//2 - radius, 
             size//2 + radius, size//2 + radius],
            outline=(r, g, b, alpha), width=2
        )
    
    # 中心圆形渐变
    center_r = size * 0.28
    for i in range(int(center_r)):
        t = i / center_r
        r = int(255 * (1-t) + 175 * t)
        g = int(45 * (1-t) + 82 * t)
        b = int(85 * (1-t) + 237 * t)
        draw.ellipse(
            [size//2 - i, size//2 - i, size//2 + i, size//2 + i],
            fill=(r, g, b, 255)
        )
    
    # 白色播放三角形
    play_size = size * 0.2
    px = size // 2 + play_size * 0.1
    py = size // 2
    points = [
        (px - play_size * 0.35, py - play_size * 0.45),
        (px + play_size * 0.55, py),
        (px - play_size * 0.35, py + play_size * 0.45),
    ]
    draw.polygon(points, fill=(255, 255, 255, 255))
    
    return img


def generate_splash(size):
    """生成启动页"""
    w, h = size
    img = Image.new('RGBA', (w, h), (0, 0, 0, 255))
    draw = ImageDraw.Draw(img)
    
    # 深色渐变背景
    for y in range(h):
        t = y / h
        r = int(18 * (1-t) + 10 * t)
        g = int(12 * (1-t) + 8 * t)
        b = int(30 * (1-t) + 20 * t)
        draw.line([(0, y), (w, y)], fill=(r, g, b, 255))
    
    # 顶部光晕
    glow_y = h * 0.35
    for i in range(60):
        alpha = int(80 * (1 - i/60))
        radius = w * 0.6 - i * 2
        draw.ellipse(
            [w//2 - radius, glow_y - radius * 0.6,
             w//2 + radius, glow_y + radius * 0.6],
            fill=(255, 45, 85, alpha) if i < 30 else (175, 82, 237, alpha)
        )
    
    # LOGO 圆形
    logo_r = min(w, h) * 0.12
    logo_cx = w // 2
    logo_cy = h * 0.38
    
    for i in range(int(logo_r)):
        t = i / logo_r
        r = int(255 * (1-t) + 175 * t)
        g = int(45 * (1-t) + 82 * t)
        b = int(85 * (1-t) + 237 * t)
        draw.ellipse(
            [logo_cx - i, logo_cy - i, logo_cx + i, logo_cy + i],
            fill=(r, g, b, 255)
        )
    
    # 播放图标
    play_s = logo_r * 0.7
    px = logo_cx + play_s * 0.1
    py = logo_cy
    points = [
        (px - play_s * 0.35, py - play_s * 0.45),
        (px + play_s * 0.55, py),
        (px - play_s * 0.35, py + play_s * 0.45),
    ]
    draw.polygon(points, fill=(255, 255, 255, 255))
    
    # APP 名称
    try:
        font_size = int(min(w, h) * 0.06)
        font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", font_size)
    except:
        font = ImageFont.load_default()
    
    text = "LIVE"
    bbox = draw.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    text_y = h * 0.55
    draw.text(((w - tw) // 2, text_y), text, fill=(255, 255, 255, 255), font=font)
    
    # 副标题
    try:
        sub_font_size = int(font_size * 0.5)
        sub_font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", sub_font_size)
    except:
        sub_font = ImageFont.load_default()
    
    sub_text = "Live Streaming Platform"
    bbox2 = draw.textbbox((0, 0), sub_text, font=sub_font)
    tw2 = bbox2[2] - bbox2[0]
    draw.text(((w - tw2) // 2, text_y + font_size * 1.3), sub_text, 
              fill=(180, 180, 200, 200), font=sub_font)
    
    # 底部加载动画点
    dot_y = h * 0.75
    dot_r = min(w, h) * 0.012
    for i in range(3):
        dx = w // 2 + (i - 1) * dot_r * 5
        alpha = 255 - i * 60
        draw.ellipse(
            [dx - dot_r, dot_y - dot_r, dx + dot_r, dot_y + dot_r],
            fill=(255, 255, 255, alpha)
        )
    
    return img


def save_icon(sizes, filenames):
    """保存多尺寸图标"""
    for size, fname in zip(sizes, filenames):
        img = generate_icon(size)
        path = os.path.join(ICON_DIR, fname)
        img.save(path, "PNG")
        print(f"  ✓ {fname} ({size}x{size})")


def save_splash(sizes, filenames):
    """保存多尺寸启动页"""
    for size, fname in zip(sizes, filenames):
        img = generate_splash(size)
        path = os.path.join(SPLASH_DIR, fname)
        img.save(path, "PNG")
        print(f"  ✓ {fname} ({size[0]}x{size[1]})")


print("=" * 50)
print("  生成 LIVE星球 APP 资源")
print("=" * 50)

print("\n📱 APP 图标:")
save_icon(
    [72, 96, 144, 192, 1024, 40, 60, 58, 87, 80, 120, 180],
    [
        "72x72.png", "96x96.png", "144x144.png", "192x192.png", "1024x1024.png",
        "40x40.png", "60x60.png", "58x58.png", "87x87.png",
        "80x80.png", "120x120.png", "180x180.png"
    ]
)

print("\n🖼️  启动页:")
save_splash(
    [(720, 1280), (1080, 1920), (1440, 2560), (750, 1334), (1242, 2208), (1125, 2436)],
    [
        "splash-720x1280.png",
        "splash-1080x1920.png", 
        "splash-1440x2560.png",
        "splash-750x1334.png",
        "splash-1242x2208.png",
        "splash-1125x2436.png"
    ]
)

# 主启动页（供 manifest 引用）
main_splash = generate_splash((1080, 1920))
main_path = os.path.join(SPLASH_DIR, "splash-bg.png")
main_splash.save(main_path, "PNG")
print(f"  ✓ splash-bg.png (1080x1920) [主启动页]")

print("\n" + "=" * 50)
print(f"  ✅ 所有资源已生成")
print(f"  📁 图标: {ICON_DIR}")
print(f"  📁 启动页: {SPLASH_DIR}")
print("=" * 50)
