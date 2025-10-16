# PixelTrace (Beta)

Inspired by something that came up during my internship at Avery Dennison, I realized that I didn't have a good program for tracing bitmaps into pixel-grid SVGs (_without_ just laying out one square for every pixel in the original image).

Time to pull a Thanos and "do it myself."

Currently writing in Java 25 on Windows 11.

## Update: Sept 22, 2024
We may now be at algorithmic completion. The introduction of hole-cutting completed the correct reproduction of opaque images. Now that transparent and translucent pixels are correctly handled, I think the algorithmic logic may be complete.

The only things that remain to be done are potentially parallelizing some of the logic to increase the speed of handling large images (or images with a lot of colors), and then making a small GUI to allow PixelTrace to be easily used as a tool.

## Update: Oct 16, 2025
After several instances of needing to include PixelTrace results in PDF files I've constructed with $\rm\LaTeX$, I've gotten tired of taking PixelTrace SVGs and feeding them through [svg2tikz](https://github.com/xyz2tex/svg2tikz). I realized I could produce much more abbreviated and condensed Ti*k*Z syntax if I simply support Ti*k*Z export as a first-party feature of PixelTrace.

So that's what I'll be working on next. After that, I'll revisit my original idea of providing a simple GUI to make PixelTrace easy to use quickly.