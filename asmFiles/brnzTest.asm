reset:
	mov	r0		x00

start:
	add	r0		x01
	comp	r0		x08
	brnz	start

end:
	jmp	reset

