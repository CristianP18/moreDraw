// floodFill.js
class FloodFill {
    constructor(ctx) {
        this.ctx = ctx;
        this.pixelStack = [];
        this.imageData = null;
        this.targetColor = null;
        this.fillColor = null;
    }

    fill(x, y, fillColor) {
        this.imageData = this.ctx.getImageData(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        const targetPixel = this.getPixel(x, y);
        this.targetColor = this.rgbToHex(targetPixel[0], targetPixel[1], targetPixel[2]);
        this.fillColor = fillColor;

        if (this.targetColor === this.fillColor) return;

        this.pixelStack.push([x, y]);
        while (this.pixelStack.length) {
            const [currentX, currentY] = this.pixelStack.pop();
            this.paintPixel(currentX, currentY);
            this.checkNeighboringPixels(currentX, currentY);
        }

        this.ctx.putImageData(this.imageData, 0, 0);
    }

    getPixel(x, y) {
        const index = (y * this.ctx.canvas.width + x) * 4;
        return [
            this.imageData.data[index], // R
            this.imageData.data[index + 1], // G
            this.imageData.data[index + 2], // B
            this.imageData.data[index + 3], // A
        ];
    }

    rgbToHex(r, g, b) {
        return `#${(1 << 24) | (r << 16) | (g << 8) | b}`
            .toString(16)
            .slice(1)
            .toUpperCase();
    }

    paintPixel(x, y) {
        const index = (y * this.ctx.canvas.width + x) * 4;
        this.imageData.data[index] = parseInt(this.fillColor.slice(1, 3), 16); // R
        this.imageData.data[index + 1] = parseInt(this.fillColor.slice(3, 5), 16); // G
        this.imageData.data[index + 2] = parseInt(this.fillColor.slice(5, 7), 16); // B
        this.imageData.data[index + 3] = 255; // A
    }

    checkNeighboringPixels(x, y) {
        if (x > 0 && this.matchColor(x - 1, y)) this.pixelStack.push([x - 1, y]);
        if (x < this.ctx.canvas.width - 1 && this.matchColor(x + 1, y)) this.pixelStack.push([x + 1, y]);
        if (y > 0 && this.matchColor(x, y - 1)) this.pixelStack.push([x, y - 1]);
        if (y < this.ctx.canvas.height - 1 && this.matchColor(x, y + 1)) this.pixelStack.push([x, y + 1]);
    }

    matchColor(x, y) {
        const pixel = this.getPixel(x, y);
        return this.rgbToHex(pixel[0], pixel[1], pixel[2]) === this.targetColor;
    }
}

export default FloodFill;
