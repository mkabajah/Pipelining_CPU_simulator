	mov	r0		x0		; r0 = x0
start:
	st		x01	r0		; MEM(1) = r0	MEM(1) = 0
	ld		r0		x01	; r4 = MEM(1)	r4 = 0
	add	r0		x01	; r4 = r4 + 1
	jmp	start
