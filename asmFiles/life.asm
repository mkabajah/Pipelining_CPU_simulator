; Insert gliders
	mov	r0		x3
	st		d35	r0
	st		d52	r0
	st		d66	r0
	st		d67	r0
	st		d68	r0

	st		d170	r0
	st		d171	r0
	st		d185	r0
	st		d187	r0
	st		d203	r0

	mov	r0		x00		; Initialise cell num

loop:

; Calc Loop, works out the next states
calc:
	mov	r1		x0			; Reset alive count
	mov	r8		r0			
	and	r8		xF0		; Upper nibble

down:
	mov	r2		r0			; Copy current cell num
	add	r2		d16		; Go down 1
	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	up
	add	r1		x1

up:
	mov	r2		r0			; Copy current cell num
	sub	r2		d16		; Go up 1
	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	left
	add	r1		x1

left:
	mov	r2		r0			; Copy current cell num
	sub	r2		d1
	and	r2		x0F
	or		r2		r8

	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	leftDown
	add	r1		x1

leftDown:
	add	r2		d16		; Go down 1
	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	leftUp
	add	r1		x1

leftUp:
	sub	r2		d32		; Go up 2
	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	right
	add	r1		x1

right:
	mov	r2		r0			; Copy current cell num
	add	r2		d1
	and	r2		x0F
	or		r2		r8

	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	rightDown
	add	r1		x1

rightDown:
	add	r2		d16		; Go down 1
	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	rightUp
	add	r1		x1

rightUp:
	sub	r2		d32		; Go up 2
	ld		r3		r2			; Load cell value
	and	r3		x3			; Check if alive
	brz	nextState
	add	r1		x1

nextState:
	ld		r3		r0			; Get cell state
	and	r3		x3			; Check if alive
	brnz	alive

dead:
	comp	r1		d3			; 3 alive neighbours?
	brz	makeAlive
	jmp	done

alive:
	comp	r1		d2			; 2 alive neighbours?
	brz	makeAlive
	comp	r1		d3			; 3 alive neighbours?
	brz	makeAlive
	jmp	done

makeAlive:
	or		r3		x30
	st		r0		r3			; Make alive

done:
	add	r0		x1			; Go To next cell
	brnz	calc


; Updates the states
update:
	ld		r1		r0			; Get state
	shr	r1		x04		; Shift next state
	st		r0		r1			; Store value

	add	r0		x1			; Go to next cell
	brnz	update

	jmp	loop

