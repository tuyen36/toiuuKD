# -*- coding: utf-8 -*-
"""Sinh bo icon launcher (dinh dang PNG) cho app."""
import os

from PIL import Image, ImageDraw

BASE = 1024
NAVY = (26, 54, 93, 255)
AMBER = (245, 166, 35, 255)
WHITE = (255, 255, 255, 255)
S = 4  # supersample

W = BASE * S


def rounded_background(size, radius_ratio=0.22):
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    r = int(size * radius_ratio)
    d.rounded_rectangle([0, 0, size - 1, size - 1], radius=r, fill=NAVY)
    return img


def draw_peak(img):
    d = ImageDraw.Draw(img)
    size = img.size[0]
    margin = 0.12
    x0, x1 = margin * size, (1 - margin) * size
    peak_y = 0.30 * size
    k = 2.9

    pts = []
    steps = 200
    for i in range(steps + 1):
        t = i / steps
        x = x0 + (x1 - x0) * t
        y = peak_y + k * ((x - 0.5 * size) ** 2) / size
        pts.append((x, y))

    lw = int(0.055 * size)
    d.line(pts, fill=WHITE, width=lw, joint='curve')
    # bo tron hai dau
    for p in (pts[0], pts[-1]):
        d.ellipse([p[0] - lw / 2, p[1] - lw / 2, p[0] + lw / 2, p[1] + lw / 2], fill=WHITE)

    # tiep tuyen ngang tai dinh
    tw = int(0.014 * size)
    d.line([(0.33 * size, peak_y), (0.67 * size, peak_y)], fill=AMBER, width=tw)

    # diem toi uu
    r = 0.085 * size
    cx, cy = 0.5 * size, peak_y
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=AMBER)


def build():
    big = rounded_background(W)
    draw_peak(big)
    big = big.resize((BASE, BASE), Image.LANCZOS)
    return big


def main():
    icon = build()
    base_dir = os.path.join('app', 'src', 'main', 'res')
    sizes = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192,
    }
    for folder, px in sizes.items():
        out_dir = os.path.join(base_dir, folder)
        os.makedirs(out_dir, exist_ok=True)
        icon.resize((px, px), Image.LANCZOS).save(os.path.join(out_dir, 'ic_launcher.png'))
        print('wrote', folder, px)

    os.makedirs('store', exist_ok=True)
    icon.save(os.path.join('store', 'icon-512.png'))
    print('wrote store/icon-512.png')


if __name__ == '__main__':
    main()
