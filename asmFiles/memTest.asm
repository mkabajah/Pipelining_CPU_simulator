mov	r0		x02	; r0 = 2
mov	r1		x04	; r1 = 4

st		x03	r0		; MEM(3) = 2
st		r0		x47	; MEM(2) = x47
st		r1		r0		; MEM(4) = 2

mov	r8		r8		; nop
mov	r8		r8		; nop
ld		r3		x02	; r3 = MEM(2) = x47
ld		r5		r1		; r5 = MEM(4) = 2
