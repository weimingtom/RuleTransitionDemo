#include <stdio.h>

#define WIDTH 640
#define HEIGHT 480
#define DEPTH 3

static unsigned char bmp_header[] = {
	0x42, 0x4D, 0x38, 0x10, 0x0E, 0x00, 0x00, 0x00, 
	0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00,  
	0x00, 0x00, (WIDTH & 0xff), (WIDTH & 0xff00) >> 8, (WIDTH & 0xff0000) >> 16, (WIDTH & 0xff000000) >> 24, (HEIGHT & 0xff), (HEIGHT & 0xff00) >> 8,
	(HEIGHT & 0xff0000) >> 16, (HEIGHT & 0xff000000) >> 24, 0x01, 0x00, 0x18, 0x00, 0x00, 0x00,  
	0x00, 0x00, 0x02, 0x10, 0x0E, 0x00, 0x12, 0x0B, 
	0x00, 0x00, 0x12, 0x0B, 0x00, 0x00, 0x00, 0x00,  
	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
};

static unsigned	BitMask[] = {
	0x2080,	// 0010 0000 1000 0000
	0xa0a0,	// 1010 0000 1010 0000
	0xa1a4,	// 1010 0001 1010 0100
	0xa5a5,	// 1010 0101 1010 0101
	0xada7,	// 1010 1101 1010 0111
	0xafaf,	// 1010 1111 1010 1111
	0xefbf,	// 1110 1111 1011 1111
	0xffff,	// 1111 1111 1111 1111
} ;
static unsigned	XMask[] = {
	0xf000, 0x0f00, 0x00f0, 0x000f,
} ;
static unsigned	YMask[] = {
	0x8888, 0x4444, 0x2222, 0x1111,
} ;

static unsigned char bmp_data[WIDTH * HEIGHT * DEPTH] = {0};

int main()
{
	FILE *fp = NULL;
	int x, y, count;
	unsigned char pixel[3] = {0};
	unsigned char *pPixel = NULL;
	unsigned char gray = 0;
	//unsigned result = 0;
	
	printf("write bmp start\n");
	if(NULL == (fp = fopen("writebmp_out.bmp", "wb+")))
	{
		printf("open file error\n");
		return 0;
	}
	fwrite(bmp_header, sizeof(bmp_header), 1, fp);
	

	pPixel = bmp_data;
	for(y = 0; y < HEIGHT; y++)
	{
		for(x = 0; x < WIDTH; x++)
		{
			gray = 0;
			gray = ((8 - y) % 8) * 32;
			gray &= 0xFF;
			//gray = 0x50;
			
			{ 
				pPixel[0] = gray;
				pPixel[1] = gray;
				pPixel[2] = gray;
			}
			pPixel += DEPTH;
		}
	}
	
	fwrite(bmp_data, sizeof(bmp_data), 1, fp);	
	fclose(fp);
	printf("write bmp end\n");
	return 0;
}


