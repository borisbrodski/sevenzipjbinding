	.file	"LzmaDec.c"
	.section	.text.unlikely,"ax",@progbits
.LCOLDB0:
	.text
.LHOTB0:
	.p2align 4,,15
	.type	LzmaDec_WriteRem, @function
LzmaDec_WriteRem:
.LFB28:
	.cfi_startproc
	movl	92(%rdi), %edx
	leal	-1(%rdx), %eax
	cmpl	$272, %eax
	ja	.L15
	movq	48(%rdi), %rax
	movl	%edx, %ecx
	xorl	%r10d, %r10d
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset 3, -16
	movq	24(%rdi), %r8
	movq	56(%rdi), %r11
	movl	76(%rdi), %r9d
	subq	%rax, %rsi
	cmpq	%rcx, %rsi
	jnb	.L3
	subl	%esi, %edx
	movl	%edx, %r10d
	movl	%esi, %edx
.L3:
	movl	68(%rdi), %ecx
	testl	%ecx, %ecx
	je	.L4
	movl	64(%rdi), %ecx
.L5:
	addl	%edx, %ecx
	testl	%edx, %edx
	movl	%r10d, 92(%rdi)
	movl	%ecx, 64(%rdi)
	je	.L10
	leaq	1(%rax), %rcx
	leal	-1(%rdx), %r10d
	xorl	%ebx, %ebx
	addq	%rcx, %r10
	jmp	.L8
	.p2align 4,,10
	.p2align 3
.L17:
	addq	$1, %rcx
.L8:
	movq	%rax, %rdx
	movq	%rbx, %rsi
	subq	%r9, %rdx
	cmpq	%rax, %r9
	cmova	%r11, %rsi
	addq	%r8, %rdx
	cmpq	%r10, %rcx
	movzbl	(%rdx,%rsi), %edx
	movb	%dl, (%r8,%rax)
	movq	%rcx, %rax
	jne	.L17
.L6:
	popq	%rbx
	.cfi_restore 3
	.cfi_def_cfa_offset 8
	movq	%r10, 48(%rdi)
.L15:
	rep ret
	.p2align 4,,10
	.p2align 3
.L4:
	.cfi_def_cfa_offset 16
	.cfi_offset 3, -16
	movl	12(%rdi), %esi
	movl	64(%rdi), %ecx
	movl	%esi, %ebx
	subl	%ecx, %ebx
	cmpl	%ebx, %edx
	jb	.L5
	movl	%esi, 68(%rdi)
	jmp	.L5
.L10:
	movq	%rax, %r10
	jmp	.L6
	.cfi_endproc
.LFE28:
	.size	LzmaDec_WriteRem, .-LzmaDec_WriteRem
	.section	.text.unlikely
.LCOLDE0:
	.text
.LHOTE0:
	.section	.text.unlikely
.LCOLDB1:
	.text
.LHOTB1:
	.p2align 4,,15
	.type	LzmaDec_TryDummy, @function
LzmaDec_TryDummy:
.LFB30:
	.cfi_startproc
	pushq	%r13
	.cfi_def_cfa_offset 16
	.cfi_offset 13, -16
	pushq	%r12
	.cfi_def_cfa_offset 24
	.cfi_offset 12, -24
	movl	$1, %eax
	pushq	%rbp
	.cfi_def_cfa_offset 32
	.cfi_offset 6, -32
	pushq	%rbx
	.cfi_def_cfa_offset 40
	.cfi_offset 3, -40
	addq	%rsi, %rdx
	movl	8(%rdi), %ecx
	movl	72(%rdi), %r9d
	movl	64(%rdi), %r11d
	movl	40(%rdi), %ebx
	movq	16(%rdi), %r10
	movl	44(%rdi), %r8d
	sall	%cl, %eax
	leal	-1(%rax), %ecx
	movl	%r9d, %eax
	sall	$4, %eax
	andl	%r11d, %ecx
	movl	%ecx, %ebp
	addq	%rbp, %rax
	cmpl	$16777215, %ebx
	leaq	(%rax,%rax), %rbp
	movzwl	(%r10,%rax,2), %r12d
	ja	.L19
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %ebx
	addq	$1, %rsi
	orl	%eax, %r8d
.L19:
	movl	%ebx, %eax
	shrl	$11, %eax
	imull	%r12d, %eax
	cmpl	%eax, %r8d
	jnb	.L21
	addq	$3692, %r10
	cmpq	$0, 64(%rdi)
	je	.L22
	movl	4(%rdi), %ecx
	movl	$1, %ebx
	movl	(%rdi), %ebp
	movq	24(%rdi), %r12
	sall	%cl, %ebx
	movl	%ebp, %ecx
	subl	$1, %ebx
	andl	%ebx, %r11d
	movq	48(%rdi), %rbx
	sall	%cl, %r11d
	testq	%rbx, %rbx
	leaq	-1(%rbx), %rcx
	je	.L102
.L24:
	movzbl	(%r12,%rcx), %ebx
	movl	$8, %ecx
	subl	%ebp, %ecx
	sarl	%cl, %ebx
	addl	%ebx, %r11d
	leal	(%r11,%r11,2), %ecx
	sall	$8, %ecx
	leaq	(%r10,%rcx,2), %r10
.L22:
	cmpl	$6, %r9d
	ja	.L25
	movl	$1, %edi
	jmp	.L29
	.p2align 4,,10
	.p2align 3
.L103:
	addl	%edi, %edi
	movl	%ecx, %eax
	cmpl	$255, %edi
	ja	.L101
.L29:
	movl	%edi, %ecx
	cmpl	$16777215, %eax
	movzwl	(%r10,%rcx,2), %ecx
	ja	.L26
	cmpq	%rdx, %rsi
	jnb	.L84
	movzbl	(%rsi), %r9d
	sall	$8, %r8d
	sall	$8, %eax
	addq	$1, %rsi
	orl	%r9d, %r8d
.L26:
	movl	%eax, %r9d
	shrl	$11, %r9d
	imull	%r9d, %ecx
	cmpl	%ecx, %r8d
	jb	.L103
	leal	1(%rdi,%rdi), %edi
	subl	%ecx, %eax
	subl	%ecx, %r8d
	cmpl	$255, %edi
	jbe	.L29
.L101:
	movl	$1, %r9d
.L30:
	cmpl	$16777215, %eax
	ja	.L87
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jb	.L87
.L20:
	popq	%rbx
	.cfi_remember_state
	.cfi_def_cfa_offset 32
	popq	%rbp
	.cfi_def_cfa_offset 24
	popq	%r12
	.cfi_def_cfa_offset 16
	popq	%r13
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L21:
	.cfi_restore_state
	subl	%eax, %ebx
	addq	$192, %r9
	subl	%eax, %r8d
	cmpl	$16777215, %ebx
	leaq	(%r9,%r9), %r11
	movzwl	(%r10,%r9,2), %edi
	ja	.L36
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %ebx
	addq	$1, %rsi
	orl	%eax, %r8d
.L36:
	movl	%ebx, %eax
	shrl	$11, %eax
	imull	%eax, %edi
	cmpl	%edi, %r8d
	jnb	.L37
	leaq	1636(%r10), %rbx
	movl	$2, %r9d
	xorl	%ebp, %ebp
.L38:
	cmpl	$16777215, %edi
	movzwl	(%rbx), %r11d
	ja	.L47
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %edi
	addq	$1, %rsi
	orl	%eax, %r8d
.L47:
	movl	%edi, %eax
	shrl	$11, %eax
	imull	%r11d, %eax
	cmpl	%eax, %r8d
	jnb	.L48
	sall	$3, %ecx
	xorl	%r13d, %r13d
	movl	$8, %r12d
	leaq	4(%rcx,%rcx), %rcx
	addq	%rcx, %rbx
.L49:
	movl	$1, %edi
	jmp	.L55
	.p2align 4,,10
	.p2align 3
.L105:
	addl	%edi, %edi
	movl	%ecx, %eax
	cmpl	%r12d, %edi
	jnb	.L104
.L55:
	movl	%edi, %ecx
	cmpl	$16777215, %eax
	movzwl	(%rbx,%rcx,2), %ecx
	ja	.L52
	cmpq	%rdx, %rsi
	jnb	.L84
	movzbl	(%rsi), %r11d
	sall	$8, %r8d
	sall	$8, %eax
	addq	$1, %rsi
	orl	%r11d, %r8d
.L52:
	movl	%eax, %r11d
	shrl	$11, %r11d
	imull	%r11d, %ecx
	cmpl	%ecx, %r8d
	jb	.L105
	leal	1(%rdi,%rdi), %edi
	subl	%ecx, %eax
	subl	%ecx, %r8d
	cmpl	%r12d, %edi
	jb	.L55
.L104:
	cmpl	$3, %ebp
	ja	.L30
	subl	%r12d, %r13d
	movl	$3, %ecx
	addl	%r13d, %edi
	cmpl	$3, %edi
	cmova	%ecx, %edi
	sall	$6, %edi
	leaq	864(%rdi,%rdi), %rbx
	movl	$1, %edi
	jmp	.L59
	.p2align 4,,10
	.p2align 3
.L107:
	addl	%edi, %edi
	movl	%ecx, %eax
.L58:
	cmpl	$63, %edi
	ja	.L106
.L59:
	movl	%edi, %ecx
	cmpl	$16777215, %eax
	leaq	(%rbx,%rcx,2), %rcx
	movzwl	(%r10,%rcx), %ecx
	ja	.L56
	cmpq	%rdx, %rsi
	jnb	.L84
	movzbl	(%rsi), %r11d
	sall	$8, %r8d
	sall	$8, %eax
	addq	$1, %rsi
	orl	%r11d, %r8d
.L56:
	movl	%eax, %r11d
	shrl	$11, %r11d
	imull	%r11d, %ecx
	cmpl	%ecx, %r8d
	jb	.L107
	subl	%ecx, %eax
	subl	%ecx, %r8d
	leal	1(%rdi,%rdi), %edi
	jmp	.L58
	.p2align 4,,10
	.p2align 3
.L87:
	popq	%rbx
	.cfi_remember_state
	.cfi_def_cfa_offset 32
	movl	%r9d, %eax
	popq	%rbp
	.cfi_def_cfa_offset 24
	popq	%r12
	.cfi_def_cfa_offset 16
	popq	%r13
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L84:
	.cfi_restore_state
	popq	%rbx
	.cfi_remember_state
	.cfi_def_cfa_offset 32
	xorl	%eax, %eax
	popq	%rbp
	.cfi_def_cfa_offset 24
	popq	%r12
	.cfi_def_cfa_offset 16
	popq	%r13
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L25:
	.cfi_restore_state
	movq	48(%rdi), %r11
	movl	76(%rdi), %r9d
	xorl	%ecx, %ecx
	movq	24(%rdi), %rbp
	movq	%r11, %rbx
	subq	%r9, %rbx
	cmpq	%r9, %r11
	jb	.L108
.L31:
	addq	%rbp, %rcx
	movl	$1, %r11d
	movl	$256, %r9d
	movzbl	(%rcx,%rbx), %ebp
	jmp	.L35
	.p2align 4,,10
	.p2align 3
.L109:
	notl	%ebx
	addl	%r11d, %r11d
	movl	%ecx, %eax
	andl	%ebx, %r9d
.L34:
	cmpl	$255, %r11d
	ja	.L101
.L35:
	addl	%ebp, %ebp
	movl	%r9d, %ebx
	movl	%r11d, %ecx
	andl	%ebp, %ebx
	movl	%r9d, %edi
	addq	%rdi, %rcx
	movl	%ebx, %r12d
	addq	%r12, %rcx
	cmpl	$16777215, %eax
	movzwl	(%r10,%rcx,2), %ecx
	ja	.L32
	cmpq	%rdx, %rsi
	jnb	.L84
	movzbl	(%rsi), %edi
	sall	$8, %r8d
	sall	$8, %eax
	addq	$1, %rsi
	orl	%edi, %r8d
.L32:
	movl	%eax, %edi
	shrl	$11, %edi
	imull	%edi, %ecx
	cmpl	%ecx, %r8d
	jb	.L109
	subl	%ecx, %eax
	subl	%ecx, %r8d
	leal	1(%r11,%r11), %r11d
	andl	%ebx, %r9d
	jmp	.L34
	.p2align 4,,10
	.p2align 3
.L37:
	subl	%edi, %ebx
	subl	%edi, %r8d
	movzwl	24(%r10,%r11), %edi
	cmpl	$16777215, %ebx
	ja	.L39
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %ebx
	addq	$1, %rsi
	orl	%eax, %r8d
.L39:
	movl	%ebx, %eax
	shrl	$11, %eax
	imull	%eax, %edi
	cmpl	%edi, %r8d
	jnb	.L40
	cmpl	$16777215, %edi
	movzwl	480(%r10,%rbp), %r9d
	ja	.L41
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %edi
	addq	$1, %rsi
	orl	%eax, %r8d
.L41:
	movl	%edi, %eax
	shrl	$11, %eax
	imull	%r9d, %eax
	cmpl	%eax, %r8d
	jnb	.L42
	cmpl	$16777215, %eax
	ja	.L86
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
.L86:
	movl	$3, %eax
	jmp	.L20
	.p2align 4,,10
	.p2align 3
.L108:
	movq	56(%rdi), %rcx
	jmp	.L31
	.p2align 4,,10
	.p2align 3
.L102:
	movq	56(%rdi), %rbx
	leaq	-1(%rbx), %rcx
	jmp	.L24
	.p2align 4,,10
	.p2align 3
.L48:
	subl	%eax, %edi
	subl	%eax, %r8d
	movzwl	2(%rbx), %r11d
	cmpl	$16777215, %edi
	ja	.L50
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %edi
	addq	$1, %rsi
	orl	%eax, %r8d
.L50:
	movl	%edi, %eax
	shrl	$11, %eax
	imull	%r11d, %eax
	cmpl	%eax, %r8d
	jnb	.L51
	sall	$3, %ecx
	movl	$8, %r13d
	movl	$8, %r12d
	leaq	260(%rcx,%rcx), %rcx
	addq	%rcx, %rbx
	jmp	.L49
	.p2align 4,,10
	.p2align 3
.L40:
	subl	%edi, %ebx
	subl	%edi, %r8d
	movzwl	48(%r10,%r11), %edi
	cmpl	$16777215, %ebx
	ja	.L45
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %ebx
	addq	$1, %rsi
	orl	%eax, %r8d
.L45:
	movl	%ebx, %eax
	shrl	$11, %eax
	imull	%eax, %edi
	cmpl	%edi, %r8d
	jb	.L44
	subl	%edi, %ebx
	subl	%edi, %r8d
	movzwl	72(%r10,%r11), %edi
	cmpl	$16777215, %ebx
	ja	.L46
	xorl	%eax, %eax
	cmpq	%rdx, %rsi
	jnb	.L20
	movzbl	(%rsi), %eax
	sall	$8, %r8d
	sall	$8, %ebx
	addq	$1, %rsi
	orl	%eax, %r8d
.L46:
	movl	%ebx, %eax
	shrl	$11, %eax
	imull	%eax, %edi
	cmpl	%edi, %r8d
	jb	.L44
	subl	%edi, %ebx
	subl	%edi, %r8d
	movl	%ebx, %edi
	.p2align 4,,10
	.p2align 3
.L44:
	leaq	2664(%r10), %rbx
	movl	$3, %r9d
	movl	$12, %ebp
	jmp	.L38
	.p2align 4,,10
	.p2align 3
.L51:
	subl	%eax, %edi
	subl	%eax, %r8d
	addq	$516, %rbx
	movl	%edi, %eax
	movl	$16, %r13d
	movl	$256, %r12d
	jmp	.L49
.L42:
	subl	%eax, %edi
	subl	%eax, %r8d
	jmp	.L44
	.p2align 4,,10
	.p2align 3
.L106:
	subl	$64, %edi
	cmpl	$3, %edi
	jbe	.L30
	movl	%edi, %ecx
	shrl	%ecx
	cmpl	$13, %edi
	ja	.L60
	movl	%edi, %r11d
	andl	$1, %edi
	subl	$1, %ecx
	orl	$2, %edi
	sall	%cl, %edi
	subq	%r11, %rdi
	leaq	1374(%rdi,%rdi), %rdi
	addq	%rdi, %r10
.L61:
	movl	$1, %r11d
	jmp	.L67
	.p2align 4,,10
	.p2align 3
.L110:
	addl	%r11d, %r11d
	movl	%edi, %eax
.L66:
	subl	$1, %ecx
	je	.L30
.L67:
	movl	%r11d, %edi
	cmpl	$16777215, %eax
	movzwl	(%r10,%rdi,2), %edi
	ja	.L64
	cmpq	%rdx, %rsi
	jnb	.L84
	movzbl	(%rsi), %ebx
	sall	$8, %r8d
	sall	$8, %eax
	addq	$1, %rsi
	orl	%ebx, %r8d
.L64:
	movl	%eax, %ebx
	shrl	$11, %ebx
	imull	%ebx, %edi
	cmpl	%edi, %r8d
	jb	.L110
	subl	%edi, %eax
	subl	%edi, %r8d
	leal	1(%r11,%r11), %r11d
	jmp	.L66
.L60:
	leal	-5(%rcx), %edi
	.p2align 4,,10
	.p2align 3
.L63:
	cmpl	$16777215, %eax
	ja	.L62
	cmpq	%rdx, %rsi
	jnb	.L84
	movzbl	(%rsi), %ecx
	sall	$8, %r8d
	sall	$8, %eax
	addq	$1, %rsi
	orl	%ecx, %r8d
.L62:
	shrl	%eax
	movl	%r8d, %ecx
	subl	%eax, %ecx
	shrl	$31, %ecx
	subl	$1, %ecx
	andl	%eax, %ecx
	subl	%ecx, %r8d
	subl	$1, %edi
	jne	.L63
	addq	$1604, %r10
	movl	$4, %ecx
	jmp	.L61
	.cfi_endproc
.LFE30:
	.size	LzmaDec_TryDummy, .-LzmaDec_TryDummy
	.section	.text.unlikely
.LCOLDE1:
	.text
.LHOTE1:
	.section	.text.unlikely
.LCOLDB2:
	.text
.LHOTB2:
	.p2align 4,,15
	.type	LzmaDec_AllocateProbs2.isra.1, @function
LzmaDec_AllocateProbs2.isra.1:
.LFB45:
	.cfi_startproc
	pushq	%r13
	.cfi_def_cfa_offset 16
	.cfi_offset 13, -16
	pushq	%r12
	.cfi_def_cfa_offset 24
	.cfi_offset 12, -24
	movq	%rsi, %rax
	pushq	%rbp
	.cfi_def_cfa_offset 32
	.cfi_offset 6, -32
	pushq	%rbx
	.cfi_def_cfa_offset 40
	.cfi_offset 3, -40
	addl	%edx, %ecx
	movl	$768, %ebx
	subq	$8, %rsp
	.cfi_def_cfa_offset 48
	movq	(%rdi), %rsi
	sall	%cl, %ebx
	addl	$1846, %ebx
	testq	%rsi, %rsi
	je	.L112
	cmpl	(%rax), %ebx
	je	.L114
.L112:
	movq	%r8, %rbp
	movq	%rax, %r13
	movq	%rdi, %r12
	movq	%r8, %rdi
	call	*8(%r8)
	movl	%ebx, %esi
	movq	$0, (%r12)
	movq	%rbp, %rdi
	addq	%rsi, %rsi
	call	*0(%rbp)
	cmpq	$1, %rax
	movq	%rax, (%r12)
	movl	%ebx, 0(%r13)
	sbbl	%eax, %eax
	addq	$8, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 40
	popq	%rbx
	.cfi_def_cfa_offset 32
	andl	$2, %eax
	popq	%rbp
	.cfi_def_cfa_offset 24
	popq	%r12
	.cfi_def_cfa_offset 16
	popq	%r13
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L114:
	.cfi_restore_state
	addq	$8, %rsp
	.cfi_def_cfa_offset 40
	xorl	%eax, %eax
	popq	%rbx
	.cfi_def_cfa_offset 32
	popq	%rbp
	.cfi_def_cfa_offset 24
	popq	%r12
	.cfi_def_cfa_offset 16
	popq	%r13
	.cfi_def_cfa_offset 8
	ret
	.cfi_endproc
.LFE45:
	.size	LzmaDec_AllocateProbs2.isra.1, .-LzmaDec_AllocateProbs2.isra.1
	.section	.text.unlikely
.LCOLDE2:
	.text
.LHOTE2:
	.section	.text.unlikely
.LCOLDB3:
	.text
.LHOTB3:
	.p2align 4,,15
	.type	LzmaProps_Decode.part.2, @function
LzmaProps_Decode.part.2:
.LFB46:
	.cfi_startproc
	movzbl	2(%rsi), %eax
	sall	$8, %eax
	movl	%eax, %edx
	movzbl	3(%rsi), %eax
	sall	$16, %eax
	orl	%edx, %eax
	movzbl	1(%rsi), %edx
	orl	%edx, %eax
	movzbl	4(%rsi), %edx
	sall	$24, %edx
	orl	%edx, %eax
	movl	$4096, %edx
	cmpl	$4095, %eax
	cmovbe	%edx, %eax
	movl	%eax, 12(%rdi)
	movzbl	(%rsi), %edx
	movl	$4, %eax
	cmpb	$-32, %dl
	jbe	.L124
	rep ret
	.p2align 4,,10
	.p2align 3
.L124:
	movzbl	%dl, %ecx
	leal	0(,%rcx,8), %eax
	subl	%ecx, %eax
	leal	(%rcx,%rax,8), %eax
	shrw	$9, %ax
	leal	(%rax,%rax,8), %ecx
	subl	%ecx, %edx
	movzbl	%al, %ecx
	movzbl	%dl, %edx
	movl	%edx, (%rdi)
	leal	(%rcx,%rcx,4), %edx
	leal	(%rcx,%rdx,8), %edx
	leal	(%rdx,%rdx,4), %edx
	shrw	$10, %dx
	movzbl	%dl, %ecx
	leal	(%rdx,%rdx,4), %edx
	movl	%ecx, 8(%rdi)
	subl	%edx, %eax
	movzbl	%al, %eax
	movl	%eax, 4(%rdi)
	xorl	%eax, %eax
	ret
	.cfi_endproc
.LFE46:
	.size	LzmaProps_Decode.part.2, .-LzmaProps_Decode.part.2
	.section	.text.unlikely
.LCOLDE3:
	.text
.LHOTE3:
	.section	.text.unlikely
.LCOLDB4:
	.text
.LHOTB4:
	.p2align 4,,15
	.type	LzmaDec_DecodeReal2, @function
LzmaDec_DecodeReal2:
.LFB29:
	.cfi_startproc
	pushq	%r15
	.cfi_def_cfa_offset 16
	.cfi_offset 15, -16
	pushq	%r14
	.cfi_def_cfa_offset 24
	.cfi_offset 14, -24
	pushq	%r13
	.cfi_def_cfa_offset 32
	.cfi_offset 13, -32
	pushq	%r12
	.cfi_def_cfa_offset 40
	.cfi_offset 12, -40
	pushq	%rbp
	.cfi_def_cfa_offset 48
	.cfi_offset 6, -48
	pushq	%rbx
	.cfi_def_cfa_offset 56
	.cfi_offset 3, -56
	subq	$120, %rsp
	.cfi_def_cfa_offset 176
	movq	32(%rdi), %rbx
	movq	48(%rdi), %r12
	movq	%rdi, 72(%rsp)
	movq	%rsi, 112(%rsp)
	movq	%rdx, 40(%rsp)
	.p2align 4,,10
	.p2align 3
.L281:
	movq	72(%rsp), %rax
	movl	68(%rax), %eax
	testl	%eax, %eax
	movl	%eax, 32(%rsp)
	movq	72(%rsp), %rax
	je	.L126
	movl	64(%rax), %eax
	movl	%eax, 8(%rsp)
	movq	112(%rsp), %rax
	movq	%rax, 16(%rsp)
.L127:
	movq	72(%rsp), %rax
	movl	$0, 56(%rsp)
	movl	76(%rax), %edx
	movq	16(%rax), %rsi
	movl	8(%rax), %ecx
	movl	%edx, 12(%rsp)
	movl	80(%rax), %edx
	movq	%rsi, %rdi
	movq	%rsi, (%rsp)
	movl	72(%rax), %esi
	movl	%edx, 64(%rsp)
	movl	84(%rax), %edx
	movl	%edx, 68(%rsp)
	movl	88(%rax), %edx
	movl	%edx, 80(%rsp)
	movq	%rax, %rdx
	movl	$1, %eax
	sall	%cl, %eax
	movl	4(%rdx), %ecx
	subl	$1, %eax
	movl	%eax, 36(%rsp)
	movl	$1, %eax
	sall	%cl, %eax
	movq	%rdi, %rcx
	leaq	1604(%rdi), %rdi
	subl	$1, %eax
	addq	$3692, %rcx
	movl	%eax, 84(%rsp)
	movq	%rdx, %rax
	movl	(%rdx), %edx
	movq	%rcx, 48(%rsp)
	movq	24(%rax), %r8
	movq	%rdi, 104(%rsp)
	movl	%edx, %r14d
	movl	%edx, 96(%rsp)
	movq	56(%rax), %rdx
	movq	%rdx, %r15
	movq	%rdx, 24(%rsp)
	movl	40(%rax), %edx
	movq	%r15, %rcx
	movl	44(%rax), %eax
	subq	$1, %rcx
	movq	%rcx, 88(%rsp)
	movl	$8, %ecx
	subl	%r14d, %ecx
	movl	%ecx, 100(%rsp)
	jmp	.L274
	.p2align 4,,10
	.p2align 3
.L314:
	movl	$2048, %edx
	subl	%r9d, %edx
	movq	48(%rsp), %r9
	shrl	$5, %edx
	addl	%edx, %edi
	movl	8(%rsp), %edx
	orl	32(%rsp), %edx
	movw	%di, 0(%rbp)
	je	.L130
	movzbl	96(%rsp), %ecx
	movl	8(%rsp), %edx
	andl	84(%rsp), %edx
	sall	%cl, %edx
	testq	%r12, %r12
	leaq	-1(%r12), %rcx
	cmove	88(%rsp), %rcx
	movzbl	(%r8,%rcx), %edi
	movzbl	100(%rsp), %ecx
	sarl	%cl, %edi
	addl	%edi, %edx
	leal	(%rdx,%rdx,2), %edx
	sall	$8, %edx
	leaq	(%r9,%rdx,2), %r9
.L130:
	addl	$1, 8(%rsp)
	cmpl	$6, %esi
	jbe	.L313
	movl	12(%rsp), %ecx
	movq	%r12, %rdx
	subq	%rcx, %rdx
	cmpq	%rcx, %r12
	movl	$0, %ecx
	cmovb	24(%rsp), %rcx
	addq	%r8, %rdx
	cmpl	$10, %esi
	movzbl	(%rdx,%rcx), %edx
	sbbl	%ecx, %ecx
	andl	$-3, %ecx
	addl	$6, %ecx
	subl	%ecx, %esi
	addl	%edx, %edx
	movl	%edx, %edi
	andl	$256, %edi
	cmpl	$16777215, %r10d
	movl	%edi, %ebp
	leaq	257(%rbp), %rcx
	leaq	(%r9,%rcx,2), %r14
	movzwl	(%r14), %r13d
	movl	%r13d, %r11d
	ja	.L160
	movzbl	(%rbx), %ecx
	sall	$8, %eax
	sall	$8, %r10d
	addq	$1, %rbx
	orl	%ecx, %eax
.L160:
	movl	%r10d, %ecx
	shrl	$11, %ecx
	imull	%r13d, %ecx
	cmpl	%eax, %ecx
	jbe	.L161
	movl	$2048, %r10d
	notl	%edi
	subl	%r13d, %r10d
	andl	$256, %edi
	shrl	$5, %r10d
	movl	%edi, %ebp
	addl	%r10d, %r11d
	movl	$2, %r10d
	movw	%r11w, (%r14)
	movl	$4, %r14d
.L162:
	addl	%edx, %edx
	movl	%edi, %r11d
	addq	%r10, %rbp
	andl	%edx, %r11d
	movl	%r11d, %r10d
	addq	%r10, %rbp
	cmpl	$16777215, %ecx
	leaq	(%r9,%rbp,2), %r15
	movzwl	(%r15), %ebp
	movl	%ebp, %r10d
	ja	.L163
	movzbl	(%rbx), %r13d
	sall	$8, %eax
	sall	$8, %ecx
	addq	$1, %rbx
	orl	%r13d, %eax
.L163:
	movl	%ecx, %r13d
	shrl	$11, %r13d
	imull	%ebp, %r13d
	cmpl	%eax, %r13d
	jbe	.L164
	movl	$2048, %ecx
	notl	%r11d
	subl	%ebp, %ecx
	andl	%r11d, %edi
	shrl	$5, %ecx
	addl	%ecx, %r10d
	movw	%r10w, (%r15)
.L165:
	addl	%edx, %edx
	movl	%edi, %r11d
	movl	%edi, %r10d
	andl	%edx, %r11d
	movl	%r14d, %ecx
	addq	%r10, %rcx
	movl	%r11d, %r10d
	addq	%r10, %rcx
	cmpl	$16777215, %r13d
	leaq	(%r9,%rcx,2), %r15
	movzwl	(%r15), %r10d
	movl	%r10d, %ecx
	ja	.L166
	movzbl	(%rbx), %ebp
	sall	$8, %eax
	sall	$8, %r13d
	addq	$1, %rbx
	orl	%ebp, %eax
.L166:
	movl	%r13d, %ebp
	shrl	$11, %ebp
	imull	%r10d, %ebp
	cmpl	%eax, %ebp
	jbe	.L167
	movl	$2048, %r13d
	notl	%r11d
	subl	%r10d, %r13d
	andl	%r11d, %edi
	movl	%r13d, %r10d
	shrl	$5, %r10d
	addl	%r10d, %ecx
	movw	%cx, (%r15)
	leal	(%r14,%r14), %ecx
.L168:
	addl	%edx, %edx
	movl	%edi, %r10d
	movl	%edi, %r13d
	andl	%edx, %r10d
	movl	%ecx, %r11d
	addq	%r13, %r11
	movl	%r10d, %r13d
	addq	%r13, %r11
	cmpl	$16777215, %ebp
	leaq	(%r9,%r11,2), %r15
	movzwl	(%r15), %r14d
	movl	%r14d, %r11d
	ja	.L169
	movzbl	(%rbx), %r13d
	sall	$8, %eax
	sall	$8, %ebp
	addq	$1, %rbx
	orl	%r13d, %eax
.L169:
	movl	%ebp, %r13d
	shrl	$11, %r13d
	imull	%r14d, %r13d
	cmpl	%eax, %r13d
	jbe	.L170
	movl	$2048, %ebp
	notl	%r10d
	addl	%ecx, %ecx
	subl	%r14d, %ebp
	shrl	$5, %ebp
	addl	%ebp, %r11d
	movw	%r11w, (%r15)
	movl	%r10d, %r11d
	andl	%edi, %r11d
.L171:
	addl	%edx, %edx
	movl	%r11d, %edi
	movl	%r11d, %ebp
	andl	%edx, %edi
	movl	%ecx, %r10d
	addq	%rbp, %r10
	movl	%edi, %ebp
	addq	%rbp, %r10
	cmpl	$16777215, %r13d
	leaq	(%r9,%r10,2), %r15
	movzwl	(%r15), %r14d
	movl	%r14d, %r10d
	ja	.L172
	movzbl	(%rbx), %ebp
	sall	$8, %eax
	sall	$8, %r13d
	addq	$1, %rbx
	orl	%ebp, %eax
.L172:
	movl	%r13d, %ebp
	shrl	$11, %ebp
	imull	%r14d, %ebp
	cmpl	%eax, %ebp
	jbe	.L173
	movl	$2048, %r13d
	notl	%edi
	addl	%ecx, %ecx
	subl	%r14d, %r13d
	andl	%r11d, %edi
	shrl	$5, %r13d
	addl	%r13d, %r10d
	movw	%r10w, (%r15)
	movl	%edi, %r10d
.L174:
	addl	%edx, %edx
	movl	%r10d, %edi
	movl	%r10d, %r13d
	andl	%edx, %edi
	movl	%ecx, %r11d
	addq	%r13, %r11
	movl	%edi, %r13d
	addq	%r13, %r11
	cmpl	$16777215, %ebp
	leaq	(%r9,%r11,2), %r15
	movzwl	(%r15), %r14d
	movl	%r14d, %r13d
	ja	.L175
	movzbl	(%rbx), %r11d
	sall	$8, %eax
	sall	$8, %ebp
	addq	$1, %rbx
	orl	%r11d, %eax
.L175:
	movl	%ebp, %r11d
	shrl	$11, %r11d
	imull	%r14d, %r11d
	cmpl	%eax, %r11d
	jbe	.L176
	movl	$2048, %ebp
	notl	%edi
	addl	%ecx, %ecx
	subl	%r14d, %ebp
	andl	%r10d, %edi
	shrl	$5, %ebp
	addl	%ebp, %r13d
	movw	%r13w, (%r15)
.L177:
	addl	%edx, %edx
	movl	%edi, %ebp
	movl	%edi, %r13d
	andl	%edx, %ebp
	movl	%ecx, %r10d
	addq	%r13, %r10
	movl	%ebp, %r13d
	addq	%r13, %r10
	cmpl	$16777215, %r11d
	leaq	(%r9,%r10,2), %r15
	movzwl	(%r15), %r14d
	movl	%r14d, %r13d
	ja	.L178
	movzbl	(%rbx), %r10d
	sall	$8, %eax
	sall	$8, %r11d
	addq	$1, %rbx
	orl	%r10d, %eax
.L178:
	movl	%r11d, %r10d
	shrl	$11, %r10d
	imull	%r14d, %r10d
	cmpl	%eax, %r10d
	jbe	.L179
	movl	$2048, %r11d
	notl	%ebp
	addl	%ecx, %ecx
	subl	%r14d, %r11d
	andl	%ebp, %edi
	shrl	$5, %r11d
	addl	%r11d, %r13d
	movw	%r13w, (%r15)
.L180:
	movl	%edi, %ebp
	movl	%ecx, %r11d
	addq	%rbp, %r11
	leal	(%rdx,%rdx), %ebp
	andl	%edi, %ebp
	addq	%r11, %rbp
	cmpl	$16777215, %r10d
	leaq	(%r9,%rbp,2), %r11
	movzwl	(%r11), %r9d
	movl	%r9d, %edi
	ja	.L181
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %r10d
	addq	$1, %rbx
	orl	%edx, %eax
.L181:
	movl	%r10d, %edx
	shrl	$11, %edx
	imull	%r9d, %edx
	cmpl	%eax, %edx
	jbe	.L182
	movl	$2048, %r14d
	addl	%ecx, %ecx
	subl	%r9d, %r14d
	movl	%r14d, %r9d
	shrl	$5, %r9d
	addl	%r9d, %edi
	movw	%di, (%r11)
.L157:
	movb	%cl, (%r8,%r12)
	addq	$1, %r12
.L183:
	cmpq	%r12, 16(%rsp)
	jbe	.L264
.L317:
	cmpq	%rbx, 40(%rsp)
	jbe	.L264
.L274:
	movl	8(%rsp), %r11d
	andl	36(%rsp), %r11d
	movl	%esi, %ecx
	sall	$4, %ecx
	movl	%r11d, %edi
	addq	%rdi, %rcx
	movq	(%rsp), %rdi
	addq	%rcx, %rcx
	cmpl	$16777215, %edx
	leaq	(%rdi,%rcx), %rbp
	movzwl	0(%rbp), %r9d
	movl	%r9d, %edi
	ja	.L128
	movzbl	(%rbx), %r10d
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%r10d, %eax
.L128:
	movl	%edx, %r10d
	shrl	$11, %r10d
	imull	%r9d, %r10d
	cmpl	%eax, %r10d
	ja	.L314
	shrl	$5, %r9d
	subl	%r10d, %edx
	subl	%r10d, %eax
	subl	%r9d, %edi
	cmpl	$16777215, %edx
	movw	%di, 0(%rbp)
	movl	%esi, %edi
	leaq	384(%rdi,%rdi), %r13
	movq	(%rsp), %rdi
	leaq	(%rdi,%r13), %rbp
	movzwl	0(%rbp), %r10d
	movl	%r10d, %r9d
	ja	.L184
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L184:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r10d, %edi
	cmpl	%eax, %edi
	jbe	.L185
	movl	$2048, %edx
	addl	$12, %esi
	subl	%r10d, %edx
	shrl	$5, %edx
	addl	%edx, %r9d
	movq	(%rsp), %rdx
	movw	%r9w, 0(%rbp)
	leaq	1636(%rdx), %r10
.L186:
	movzwl	(%r10), %r9d
	cmpl	$16777215, %edi
	movl	%r9d, %ecx
	ja	.L199
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%edx, %eax
.L199:
	movl	%edi, %ebp
	shrl	$11, %ebp
	imull	%r9d, %ebp
	cmpl	%eax, %ebp
	jbe	.L200
	movl	$2048, %edx
	sall	$3, %r11d
	subl	%r9d, %edx
	shrl	$5, %edx
	addl	%edx, %ecx
	leaq	4(%r11,%r11), %rdx
	movw	%cx, (%r10)
	addq	%rdx, %r10
	cmpl	$16777215, %ebp
	movzwl	2(%r10), %edi
	movl	%edi, %ecx
	ja	.L201
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %ebp
	addq	$1, %rbx
	orl	%edx, %eax
.L201:
	movl	%ebp, %edx
	shrl	$11, %edx
	imull	%edi, %edx
	cmpl	%eax, %edx
	jbe	.L202
	movl	$2048, %r14d
	movl	$4, %r9d
	subl	%edi, %r14d
	movl	%r14d, %edi
	shrl	$5, %edi
	addl	%edi, %ecx
	movl	$4, %edi
	movw	%cx, 2(%r10)
.L203:
	addq	%r10, %r9
	cmpl	$16777215, %edx
	movzwl	(%r9), %ebp
	movl	%ebp, %r11d
	ja	.L204
	movzbl	(%rbx), %ecx
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%ecx, %eax
.L204:
	movl	%edx, %ecx
	shrl	$11, %ecx
	imull	%ebp, %ecx
	cmpl	%eax, %ecx
	jbe	.L205
	movl	$2048, %edx
	subl	%ebp, %edx
	shrl	$5, %edx
	addl	%edx, %r11d
	movw	%r11w, (%r9)
.L206:
	movl	%edi, %edx
	cmpl	$16777215, %ecx
	leaq	(%r10,%rdx,2), %r11
	movzwl	(%r11), %r10d
	movl	%r10d, %r9d
	ja	.L207
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %ecx
	addq	$1, %rbx
	orl	%edx, %eax
.L207:
	movl	%ecx, %edx
	shrl	$11, %edx
	imull	%r10d, %edx
	cmpl	%eax, %edx
	jbe	.L208
	movl	$2048, %ecx
	addl	%edi, %edi
	subl	%r10d, %ecx
	shrl	$5, %ecx
	addl	%ecx, %r9d
	movw	%r9w, (%r11)
.L209:
	leal	-8(%rdi), %r15d
.L210:
	cmpl	$11, %esi
	jbe	.L225
	cmpl	$3, %r15d
	movl	$3, %ecx
	cmovbe	%r15d, %ecx
	sall	$6, %ecx
	leaq	864(%rcx,%rcx), %rcx
	addq	(%rsp), %rcx
	cmpl	$16777215, %edx
	movzwl	2(%rcx), %r10d
	movl	%r10d, %r9d
	ja	.L226
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L226:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r10d, %edi
	cmpl	%eax, %edi
	jbe	.L227
	movl	$2048, %edx
	subl	%r10d, %edx
	movl	$4, %r10d
	shrl	$5, %edx
	addl	%edx, %r9d
	movl	$4, %edx
	movw	%r9w, 2(%rcx)
.L228:
	addq	%rcx, %r10
	cmpl	$16777215, %edi
	movzwl	(%r10), %ebp
	movl	%ebp, %r11d
	ja	.L229
	movzbl	(%rbx), %r9d
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%r9d, %eax
.L229:
	movl	%edi, %r9d
	shrl	$11, %r9d
	imull	%ebp, %r9d
	cmpl	%eax, %r9d
	jbe	.L230
	movl	$2048, %edi
	subl	%ebp, %edi
	shrl	$5, %edi
	addl	%edi, %r11d
	movw	%r11w, (%r10)
.L231:
	movl	%edx, %edi
	cmpl	$16777215, %r9d
	leaq	(%rcx,%rdi,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L232
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %r9d
	addq	$1, %rbx
	orl	%edi, %eax
.L232:
	movl	%r9d, %edi
	shrl	$11, %edi
	imull	%r11d, %edi
	cmpl	%eax, %edi
	jbe	.L233
	movl	$2048, %r9d
	addl	%edx, %edx
	subl	%r11d, %r9d
	shrl	$5, %r9d
	addl	%r9d, %r10d
	movw	%r10w, 0(%rbp)
.L234:
	movl	%edx, %r9d
	cmpl	$16777215, %edi
	leaq	(%rcx,%r9,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r9d
	ja	.L235
	movzbl	(%rbx), %r10d
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%r10d, %eax
.L235:
	movl	%edi, %r10d
	shrl	$11, %r10d
	imull	%r11d, %r10d
	cmpl	%eax, %r10d
	jbe	.L236
	movl	$2048, %edi
	addl	%edx, %edx
	subl	%r11d, %edi
	shrl	$5, %edi
	addl	%edi, %r9d
	movw	%r9w, 0(%rbp)
.L237:
	movl	%edx, %edi
	cmpl	$16777215, %r10d
	leaq	(%rcx,%rdi,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %edi
	ja	.L238
	movzbl	(%rbx), %r9d
	sall	$8, %eax
	sall	$8, %r10d
	addq	$1, %rbx
	orl	%r9d, %eax
.L238:
	movl	%r10d, %r9d
	shrl	$11, %r9d
	imull	%r11d, %r9d
	cmpl	%eax, %r9d
	jbe	.L239
	movl	$2048, %r10d
	subl	%r11d, %r10d
	shrl	$5, %r10d
	addl	%r10d, %edi
	movw	%di, 0(%rbp)
	leal	(%rdx,%rdx), %edi
.L240:
	movl	%edi, %edx
	cmpl	$16777215, %r9d
	leaq	(%rcx,%rdx,2), %r11
	movzwl	(%r11), %r10d
	movl	%r10d, %ecx
	ja	.L241
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %r9d
	addq	$1, %rbx
	orl	%edx, %eax
.L241:
	movl	%r9d, %edx
	shrl	$11, %edx
	imull	%r10d, %edx
	cmpl	%eax, %edx
	jbe	.L242
	movl	$2048, %r9d
	addl	%edi, %edi
	subl	%r10d, %r9d
	shrl	$5, %r9d
	addl	%r9d, %ecx
	movw	%cx, (%r11)
.L243:
	subl	$64, %edi
	cmpl	$3, %edi
	jbe	.L244
	movl	%edi, %r9d
	movl	%edi, %ecx
	andl	$1, %r9d
	shrl	%ecx
	orl	$2, %r9d
	cmpl	$13, %edi
	ja	.L245
	subl	$1, %ecx
	movl	$688, %r10d
	movl	$1, %r13d
	sall	%cl, %r9d
	subq	%rdi, %r10
	movl	$1, %r14d
	movl	%r9d, %edi
	movq	(%rsp), %r11
	movq	%r8, 56(%rsp)
	addq	%r10, %rdi
	movl	%esi, 80(%rsp)
	leaq	-2(%rdi,%rdi), %rdi
	movq	%rdi, %rbp
	movl	%r9d, %edi
	jmp	.L249
	.p2align 4,,10
	.p2align 3
.L316:
	movl	$2048, %edx
	addl	%r13d, %r13d
	addl	%r14d, %r14d
	subl	%r10d, %edx
	shrl	$5, %edx
	addl	%edx, %r9d
	subl	$1, %ecx
	movl	%esi, %edx
	movw	%r9w, (%r8)
	je	.L315
.L249:
	movl	%r13d, %esi
	leaq	0(%rbp,%rsi,2), %r8
	addq	%r11, %r8
	cmpl	$16777215, %edx
	movzwl	(%r8), %r10d
	movl	%r10d, %r9d
	ja	.L246
	movzbl	(%rbx), %esi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%esi, %eax
.L246:
	movl	%edx, %esi
	shrl	$11, %esi
	imull	%r10d, %esi
	cmpl	%eax, %esi
	ja	.L316
	shrl	$5, %r10d
	orl	%r14d, %edi
	subl	%esi, %edx
	subl	%r10d, %r9d
	subl	%esi, %eax
	addl	%r14d, %r14d
	subl	$1, %ecx
	movw	%r9w, (%r8)
	leal	1(%r13,%r13), %r13d
	jne	.L249
.L315:
	movq	56(%rsp), %r8
	movl	80(%rsp), %esi
.L244:
	movl	32(%rsp), %r9d
	leal	1(%rdi), %ecx
	testl	%r9d, %r9d
	jne	.L265
	cmpl	%edi, 8(%rsp)
	jbe	.L312
.L266:
	cmpl	$18, %esi
	movl	68(%rsp), %esi
	movl	%esi, 80(%rsp)
	movl	64(%rsp), %esi
	movl	%esi, 68(%rsp)
	movl	12(%rsp), %esi
	movl	%ecx, 12(%rsp)
	movl	%esi, 64(%rsp)
	jbe	.L289
	movl	$10, %esi
.L225:
	movq	16(%rsp), %rcx
	leal	2(%r15), %edi
	subq	%r12, %rcx
	je	.L312
	movl	%edi, %r10d
	movl	$0, 56(%rsp)
	cmpq	%r10, %rcx
	jnb	.L268
	subl	%ecx, %edi
	movq	%rcx, %r10
	movl	%edi, 56(%rsp)
	movl	%ecx, %edi
.L268:
	movl	12(%rsp), %r9d
	movq	24(%rsp), %r14
	movq	%r12, %rcx
	subq	%r9, %rcx
	cmpq	%r9, %r12
	movl	$0, %r9d
	cmovb	%r14, %r9
	addl	%edi, 8(%rsp)
	addq	%r9, %rcx
	movq	%r14, %r9
	subq	%rcx, %r9
	cmpq	%r10, %r9
	jb	.L270
	leaq	(%r8,%r12), %rdi
	subq	%r12, %rcx
	addq	%r10, %r12
	leaq	(%rdi,%r10), %r9
	.p2align 4,,10
	.p2align 3
.L271:
	movzbl	(%rdi,%rcx), %r10d
	addq	$1, %rdi
	movb	%r10b, -1(%rdi)
	cmpq	%r9, %rdi
	jne	.L271
	cmpq	%r12, 16(%rsp)
	ja	.L317
.L264:
	cmpl	$16777215, %edx
	ja	.L276
	movzbl	(%rbx), %ecx
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%ecx, %eax
.L276:
	movq	72(%rsp), %rcx
	movl	%eax, 44(%rcx)
	movl	56(%rsp), %eax
	movq	%rbx, 32(%rcx)
	movl	%edx, 40(%rcx)
	movq	%r12, 48(%rcx)
	movl	%esi, 72(%rcx)
	movl	%eax, 92(%rcx)
	movl	8(%rsp), %eax
	movl	%eax, 64(%rcx)
	movl	12(%rsp), %eax
	movl	%eax, 76(%rcx)
	movl	64(%rsp), %eax
	movl	%eax, 80(%rcx)
	movl	68(%rsp), %eax
	movl	%eax, 84(%rcx)
	movl	80(%rsp), %eax
	movl	%eax, 88(%rcx)
	movl	68(%rcx), %eax
	testl	%eax, %eax
	jne	.L277
	movl	12(%rcx), %eax
	cmpl	8(%rsp), %eax
	ja	.L277
	movl	%eax, 68(%rcx)
.L277:
	movq	112(%rsp), %r14
	movq	72(%rsp), %rbx
	movq	%r14, %rsi
	movq	%rbx, %rdi
	call	LzmaDec_WriteRem
	movq	48(%rbx), %r12
	cmpq	%r12, %r14
	jbe	.L280
	movq	72(%rsp), %rax
	movq	32(%rax), %rbx
	cmpq	%rbx, 40(%rsp)
	jbe	.L280
	movl	92(%rax), %ecx
	cmpl	$273, %ecx
	jbe	.L281
	xorl	%eax, %eax
	cmpl	$274, %ecx
	jbe	.L299
.L321:
	movq	72(%rsp), %rbx
	movl	$274, 92(%rbx)
	addq	$120, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L185:
	.cfi_restore_state
	shrl	$5, %r10d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r10d, %r9d
	movl	8(%rsp), %edi
	orl	32(%rsp), %edi
	movw	%r9w, 0(%rbp)
	je	.L187
	movq	(%rsp), %rdi
	cmpl	$16777215, %edx
	leaq	24(%rdi,%r13), %rbp
	movzwl	0(%rbp), %r10d
	movl	%r10d, %r9d
	ja	.L188
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L188:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r10d, %edi
	cmpl	%eax, %edi
	jbe	.L189
	movl	$2048, %edx
	subl	%r10d, %edx
	shrl	$5, %edx
	addl	%edx, %r9d
	movq	(%rsp), %rdx
	cmpl	$16777215, %edi
	movw	%r9w, 0(%rbp)
	leaq	480(%rdx,%rcx), %r10
	movzwl	(%r10), %r9d
	movl	%r9d, %ecx
	ja	.L190
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%edx, %eax
.L190:
	movl	%edi, %edx
	shrl	$11, %edx
	imull	%r9d, %edx
	cmpl	%eax, %edx
	jbe	.L191
	movl	$2048, %edi
	subl	%r9d, %edi
	shrl	$5, %edi
	addl	%edi, %ecx
	movl	12(%rsp), %edi
	movw	%cx, (%r10)
	movq	%r12, %rcx
	subq	%rdi, %rcx
	cmpq	%rdi, %r12
	movl	$0, %edi
	cmovb	24(%rsp), %rdi
	addq	%r8, %rcx
	addl	$1, 8(%rsp)
	movzbl	(%rcx,%rdi), %ecx
	movb	%cl, (%r8,%r12)
	addq	$1, %r12
	cmpl	$7, %esi
	sbbl	%esi, %esi
	andl	$-2, %esi
	addl	$11, %esi
	jmp	.L183
	.p2align 4,,10
	.p2align 3
.L200:
	subl	%ebp, %edi
	subl	%ebp, %eax
	movzwl	2(%r10), %ebp
	shrl	$5, %r9d
	subl	%r9d, %ecx
	cmpl	$16777215, %edi
	movw	%cx, (%r10)
	movl	%ebp, %r9d
	ja	.L211
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%edx, %eax
.L211:
	movl	%edi, %ecx
	shrl	$11, %ecx
	imull	%ebp, %ecx
	cmpl	%eax, %ecx
	jbe	.L212
	movl	$2048, %edx
	sall	$3, %r11d
	subl	%ebp, %edx
	shrl	$5, %edx
	addl	%edx, %r9d
	leaq	260(%r11,%r11), %rdx
	movw	%r9w, 2(%r10)
	addq	%rdx, %r10
	cmpl	$16777215, %ecx
	movzwl	2(%r10), %r9d
	movl	%r9d, %edi
	ja	.L213
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %ecx
	addq	$1, %rbx
	orl	%edx, %eax
.L213:
	movl	%ecx, %edx
	shrl	$11, %edx
	imull	%r9d, %edx
	cmpl	%eax, %edx
	jbe	.L214
	movl	$2048, %ecx
	subl	%r9d, %ecx
	movl	$4, %r9d
	shrl	$5, %ecx
	addl	%ecx, %edi
	movw	%di, 2(%r10)
	movl	$4, %edi
.L215:
	addq	%r10, %rdi
	cmpl	$16777215, %edx
	movzwl	(%rdi), %ebp
	movl	%ebp, %r11d
	ja	.L216
	movzbl	(%rbx), %ecx
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%ecx, %eax
.L216:
	movl	%edx, %ecx
	shrl	$11, %ecx
	imull	%ebp, %ecx
	cmpl	%eax, %ecx
	jbe	.L217
	movl	$2048, %edx
	subl	%ebp, %edx
	shrl	$5, %edx
	addl	%edx, %r11d
	movw	%r11w, (%rdi)
.L218:
	movl	%r9d, %edx
	cmpl	$16777215, %ecx
	leaq	(%r10,%rdx,2), %r11
	movzwl	(%r11), %r10d
	movl	%r10d, %edi
	ja	.L219
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %ecx
	addq	$1, %rbx
	orl	%edx, %eax
.L219:
	movl	%ecx, %edx
	shrl	$11, %edx
	imull	%r10d, %edx
	cmpl	%eax, %edx
	jbe	.L220
	movl	$2048, %ecx
	leal	(%r9,%r9), %r15d
	subl	%r10d, %ecx
	shrl	$5, %ecx
	addl	%ecx, %edi
	movw	%di, (%r11)
	jmp	.L210
	.p2align 4,,10
	.p2align 3
.L270:
	subl	$1, %edi
	leaq	(%r8,%r12), %r9
	movq	24(%rsp), %r11
	leaq	1(%r12,%rdi), %r12
	leaq	(%r8,%r12), %rdi
	.p2align 4,,10
	.p2align 3
.L273:
	movzbl	(%r8,%rcx), %r10d
	addq	$1, %rcx
	movl	$0, %r15d
	cmpq	%rcx, %r11
	cmove	%r15, %rcx
	addq	$1, %r9
	movb	%r10b, -1(%r9)
	cmpq	%rdi, %r9
	jne	.L273
	jmp	.L183
	.p2align 4,,10
	.p2align 3
.L173:
	shrl	$5, %r14d
	andl	%r11d, %edi
	subl	%ebp, %r13d
	subl	%r14d, %r10d
	subl	%ebp, %eax
	leal	1(%rcx,%rcx), %ecx
	movw	%r10w, (%r15)
	movl	%r13d, %ebp
	movl	%edi, %r10d
	jmp	.L174
	.p2align 4,,10
	.p2align 3
.L170:
	shrl	$5, %r14d
	subl	%r13d, %ebp
	subl	%r13d, %eax
	subl	%r14d, %r11d
	leal	1(%rcx,%rcx), %ecx
	movl	%ebp, %r13d
	movw	%r11w, (%r15)
	movl	%r10d, %r11d
	andl	%edi, %r11d
	jmp	.L171
	.p2align 4,,10
	.p2align 3
.L167:
	shrl	$5, %r10d
	subl	%ebp, %r13d
	subl	%ebp, %eax
	subl	%r10d, %ecx
	andl	%r11d, %edi
	movl	%r13d, %ebp
	movw	%cx, (%r15)
	leal	1(%r14,%r14), %ecx
	jmp	.L168
	.p2align 4,,10
	.p2align 3
.L164:
	shrl	$5, %ebp
	subl	%r13d, %ecx
	subl	%r13d, %eax
	subl	%ebp, %r10d
	addl	$1, %r14d
	andl	%r11d, %edi
	movw	%r10w, (%r15)
	movl	%ecx, %r13d
	jmp	.L165
	.p2align 4,,10
	.p2align 3
.L161:
	shrl	$5, %r13d
	subl	%ecx, %r10d
	subl	%ecx, %eax
	subl	%r13d, %r11d
	movl	%r10d, %ecx
	movl	$3, %r10d
	movw	%r11w, (%r14)
	movl	$6, %r14d
	jmp	.L162
	.p2align 4,,10
	.p2align 3
.L182:
	shrl	$5, %r9d
	subl	%edx, %r10d
	subl	%edx, %eax
	subl	%r9d, %edi
	leal	1(%rcx,%rcx), %ecx
	movl	%r10d, %edx
	movw	%di, (%r11)
	jmp	.L157
	.p2align 4,,10
	.p2align 3
.L179:
	shrl	$5, %r14d
	subl	%r10d, %r11d
	subl	%r10d, %eax
	subl	%r14d, %r13d
	leal	1(%rcx,%rcx), %ecx
	andl	%ebp, %edi
	movw	%r13w, (%r15)
	movl	%r11d, %r10d
	jmp	.L180
	.p2align 4,,10
	.p2align 3
.L176:
	shrl	$5, %r14d
	subl	%r11d, %ebp
	subl	%r11d, %eax
	subl	%r14d, %r13d
	leal	1(%rcx,%rcx), %ecx
	andl	%r10d, %edi
	movw	%r13w, (%r15)
	movl	%ebp, %r11d
	jmp	.L177
	.p2align 4,,10
	.p2align 3
.L313:
	movzwl	2(%r9), %edi
	cmpl	$3, %esi
	movl	$3, %edx
	cmovbe	%esi, %edx
	subl	%edx, %esi
	cmpl	$16777215, %r10d
	movl	%edi, %ecx
	ja	.L134
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %r10d
	addq	$1, %rbx
	orl	%edx, %eax
.L134:
	movl	%r10d, %edx
	shrl	$11, %edx
	imull	%edi, %edx
	cmpl	%eax, %edx
	jbe	.L135
	movl	$2048, %r14d
	movl	$4, %r10d
	subl	%edi, %r14d
	movl	%r14d, %edi
	shrl	$5, %edi
	addl	%edi, %ecx
	movw	%cx, 2(%r9)
	movl	$4, %ecx
.L136:
	addq	%r9, %r10
	cmpl	$16777215, %edx
	movzwl	(%r10), %ebp
	movl	%ebp, %r11d
	ja	.L137
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L137:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%ebp, %edi
	cmpl	%eax, %edi
	jbe	.L138
	movl	$2048, %edx
	subl	%ebp, %edx
	shrl	$5, %edx
	addl	%edx, %r11d
	movw	%r11w, (%r10)
.L139:
	movl	%ecx, %edx
	cmpl	$16777215, %edi
	leaq	(%r9,%rdx,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L140
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%edx, %eax
.L140:
	movl	%edi, %edx
	shrl	$11, %edx
	imull	%r11d, %edx
	cmpl	%eax, %edx
	jbe	.L141
	movl	$2048, %edi
	addl	%ecx, %ecx
	subl	%r11d, %edi
	shrl	$5, %edi
	addl	%edi, %r10d
	movw	%r10w, 0(%rbp)
.L142:
	movl	%ecx, %edi
	cmpl	$16777215, %edx
	leaq	(%r9,%rdi,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L143
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L143:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r11d, %edi
	cmpl	%eax, %edi
	jbe	.L144
	movl	$2048, %edx
	addl	%ecx, %ecx
	subl	%r11d, %edx
	shrl	$5, %edx
	addl	%edx, %r10d
	movw	%r10w, 0(%rbp)
.L145:
	movl	%ecx, %edx
	cmpl	$16777215, %edi
	leaq	(%r9,%rdx,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L146
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%edx, %eax
.L146:
	movl	%edi, %edx
	shrl	$11, %edx
	imull	%r11d, %edx
	cmpl	%eax, %edx
	jbe	.L147
	movl	$2048, %edi
	addl	%ecx, %ecx
	subl	%r11d, %edi
	shrl	$5, %edi
	addl	%edi, %r10d
	movw	%r10w, 0(%rbp)
.L148:
	movl	%ecx, %edi
	cmpl	$16777215, %edx
	leaq	(%r9,%rdi,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L149
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L149:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r11d, %edi
	cmpl	%eax, %edi
	jbe	.L150
	movl	$2048, %edx
	addl	%ecx, %ecx
	subl	%r11d, %edx
	shrl	$5, %edx
	addl	%edx, %r10d
	movw	%r10w, 0(%rbp)
.L151:
	movl	%ecx, %edx
	cmpl	$16777215, %edi
	leaq	(%r9,%rdx,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L152
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %edi
	addq	$1, %rbx
	orl	%edx, %eax
.L152:
	movl	%edi, %edx
	shrl	$11, %edx
	imull	%r11d, %edx
	cmpl	%eax, %edx
	jbe	.L153
	movl	$2048, %edi
	addl	%ecx, %ecx
	subl	%r11d, %edi
	shrl	$5, %edi
	addl	%edi, %r10d
	movw	%r10w, 0(%rbp)
.L154:
	movl	%ecx, %edi
	cmpl	$16777215, %edx
	leaq	(%r9,%rdi,2), %r11
	movzwl	(%r11), %r10d
	movl	%r10d, %r9d
	ja	.L155
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L155:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r10d, %edi
	cmpl	%eax, %edi
	ja	.L318
	shrl	$5, %r10d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r10d, %r9d
	leal	1(%rcx,%rcx), %ecx
	movw	%r9w, (%r11)
	jmp	.L157
	.p2align 4,,10
	.p2align 3
.L189:
	movq	(%rsp), %rcx
	shrl	$5, %r10d
	subl	%edi, %edx
	subl	%r10d, %r9d
	subl	%edi, %eax
	cmpl	$16777215, %edx
	movw	%r9w, 0(%rbp)
	leaq	48(%rcx,%r13), %r10
	movzwl	(%r10), %r9d
	movl	%r9d, %ecx
	ja	.L194
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L194:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r9d, %edi
	cmpl	%eax, %edi
	jbe	.L195
	movl	$2048, %edx
	subl	%r9d, %edx
	shrl	$5, %edx
	addl	%edx, %ecx
	movl	12(%rsp), %edx
	movw	%cx, (%r10)
	movl	64(%rsp), %ecx
	movl	%edx, 64(%rsp)
	movl	%ecx, 12(%rsp)
.L193:
	cmpl	$7, %esi
	movq	(%rsp), %rdx
	sbbl	%esi, %esi
	andl	$-3, %esi
	addl	$11, %esi
	leaq	2664(%rdx), %r10
	jmp	.L186
	.p2align 4,,10
	.p2align 3
.L208:
	shrl	$5, %r10d
	subl	%edx, %ecx
	subl	%edx, %eax
	subl	%r10d, %r9d
	leal	1(%rdi,%rdi), %edi
	movl	%ecx, %edx
	movw	%r9w, (%r11)
	jmp	.L209
	.p2align 4,,10
	.p2align 3
.L202:
	shrl	$5, %edi
	subl	%edx, %ebp
	subl	%edx, %eax
	subl	%edi, %ecx
	movl	%ebp, %edx
	movl	$6, %edi
	movw	%cx, 2(%r10)
	movl	$6, %r9d
	jmp	.L203
	.p2align 4,,10
	.p2align 3
.L205:
	shrl	$5, %ebp
	subl	%ecx, %edx
	subl	%ecx, %eax
	subl	%ebp, %r11d
	addl	$1, %edi
	movl	%edx, %ecx
	movw	%r11w, (%r9)
	jmp	.L206
	.p2align 4,,10
	.p2align 3
.L233:
	shrl	$5, %r11d
	subl	%edi, %r9d
	subl	%edi, %eax
	subl	%r11d, %r10d
	leal	1(%rdx,%rdx), %edx
	movl	%r9d, %edi
	movw	%r10w, 0(%rbp)
	jmp	.L234
	.p2align 4,,10
	.p2align 3
.L230:
	shrl	$5, %ebp
	subl	%r9d, %edi
	subl	%r9d, %eax
	subl	%ebp, %r11d
	addl	$1, %edx
	movl	%edi, %r9d
	movw	%r11w, (%r10)
	jmp	.L231
	.p2align 4,,10
	.p2align 3
.L239:
	shrl	$5, %r11d
	subl	%r9d, %r10d
	subl	%r9d, %eax
	subl	%r11d, %edi
	movl	%r10d, %r9d
	movw	%di, 0(%rbp)
	leal	1(%rdx,%rdx), %edi
	jmp	.L240
	.p2align 4,,10
	.p2align 3
.L236:
	shrl	$5, %r11d
	subl	%r10d, %edi
	subl	%r10d, %eax
	subl	%r11d, %r9d
	leal	1(%rdx,%rdx), %edx
	movl	%edi, %r10d
	movw	%r9w, 0(%rbp)
	jmp	.L237
	.p2align 4,,10
	.p2align 3
.L242:
	shrl	$5, %r10d
	subl	%edx, %r9d
	subl	%edx, %eax
	subl	%r10d, %ecx
	leal	1(%rdi,%rdi), %edi
	movl	%r9d, %edx
	movw	%cx, (%r11)
	jmp	.L243
	.p2align 4,,10
	.p2align 3
.L212:
	shrl	$5, %ebp
	movl	%edi, %edx
	subl	%ecx, %eax
	subl	%ebp, %r9d
	subl	%ecx, %edx
	movl	$1, %r11d
	movw	%r9w, 2(%r10)
	jmp	.L224
	.p2align 4,,10
	.p2align 3
.L320:
	movl	$2048, %edx
	addl	%r11d, %r11d
	subl	%ebp, %edx
	shrl	$5, %edx
	addl	%edx, %r9d
	cmpl	$255, %r11d
	movl	%ecx, %edx
	movw	%r9w, (%rdi)
	ja	.L319
.L224:
	movl	%r11d, %ecx
	leaq	516(%rcx,%rcx), %rdi
	addq	%r10, %rdi
	cmpl	$16777215, %edx
	movzwl	(%rdi), %ebp
	movl	%ebp, %r9d
	ja	.L221
	movzbl	(%rbx), %ecx
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%ecx, %eax
.L221:
	movl	%edx, %ecx
	shrl	$11, %ecx
	imull	%ebp, %ecx
	cmpl	%eax, %ecx
	ja	.L320
	leal	1(%r11,%r11), %r11d
	shrl	$5, %ebp
	subl	%ecx, %edx
	subl	%ebp, %r9d
	subl	%ecx, %eax
	cmpl	$255, %r11d
	movw	%r9w, (%rdi)
	jbe	.L224
.L319:
	leal	-240(%r11), %r15d
	jmp	.L210
	.p2align 4,,10
	.p2align 3
.L227:
	shrl	$5, %r10d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r10d, %r9d
	movl	%edx, %edi
	movl	$6, %r10d
	movw	%r9w, 2(%rcx)
	movl	$6, %edx
	jmp	.L228
	.p2align 4,,10
	.p2align 3
.L265:
	cmpl	%edi, 32(%rsp)
	ja	.L266
.L312:
	movq	72(%rsp), %rax
	movq	%r12, 48(%rax)
.L187:
	movl	$1, %eax
.L299:
	addq	$120, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L289:
	.cfi_restore_state
	movl	$7, %esi
	jmp	.L225
	.p2align 4,,10
	.p2align 3
.L135:
	shrl	$5, %edi
	subl	%edx, %r10d
	subl	%edx, %eax
	subl	%edi, %ecx
	movl	%r10d, %edx
	movl	$6, %r10d
	movw	%cx, 2(%r9)
	movl	$6, %ecx
	jmp	.L136
	.p2align 4,,10
	.p2align 3
.L147:
	shrl	$5, %r11d
	subl	%edx, %edi
	subl	%edx, %eax
	subl	%r11d, %r10d
	leal	1(%rcx,%rcx), %ecx
	movl	%edi, %edx
	movw	%r10w, 0(%rbp)
	jmp	.L148
	.p2align 4,,10
	.p2align 3
.L144:
	shrl	$5, %r11d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r11d, %r10d
	leal	1(%rcx,%rcx), %ecx
	movl	%edx, %edi
	movw	%r10w, 0(%rbp)
	jmp	.L145
	.p2align 4,,10
	.p2align 3
.L141:
	shrl	$5, %r11d
	subl	%edx, %edi
	subl	%edx, %eax
	subl	%r11d, %r10d
	leal	1(%rcx,%rcx), %ecx
	movl	%edi, %edx
	movw	%r10w, 0(%rbp)
	jmp	.L142
	.p2align 4,,10
	.p2align 3
.L138:
	shrl	$5, %ebp
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%ebp, %r11d
	addl	$1, %ecx
	movl	%edx, %edi
	movw	%r11w, (%r10)
	jmp	.L139
	.p2align 4,,10
	.p2align 3
.L153:
	shrl	$5, %r11d
	subl	%edx, %edi
	subl	%edx, %eax
	subl	%r11d, %r10d
	leal	1(%rcx,%rcx), %ecx
	movl	%edi, %edx
	movw	%r10w, 0(%rbp)
	jmp	.L154
	.p2align 4,,10
	.p2align 3
.L150:
	shrl	$5, %r11d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r11d, %r10d
	leal	1(%rcx,%rcx), %ecx
	movl	%edx, %edi
	movw	%r10w, 0(%rbp)
	jmp	.L151
	.p2align 4,,10
	.p2align 3
.L191:
	shrl	$5, %r9d
	subl	%edx, %edi
	subl	%edx, %eax
	subl	%r9d, %ecx
	movw	%cx, (%r10)
	jmp	.L193
	.p2align 4,,10
	.p2align 3
.L195:
	shrl	$5, %r9d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r9d, %ecx
	cmpl	$16777215, %edx
	movw	%cx, (%r10)
	movq	(%rsp), %rcx
	leaq	72(%rcx,%r13), %r10
	movzwl	(%r10), %r9d
	movl	%r9d, %ecx
	ja	.L196
	movzbl	(%rbx), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%edi, %eax
.L196:
	movl	%edx, %edi
	shrl	$11, %edi
	imull	%r9d, %edi
	cmpl	%eax, %edi
	jbe	.L197
	movl	$2048, %edx
	subl	%r9d, %edx
	shrl	$5, %edx
	addl	%ecx, %edx
	movl	68(%rsp), %ecx
	movw	%dx, (%r10)
	movl	12(%rsp), %edx
	movl	%ecx, 12(%rsp)
	movl	64(%rsp), %ecx
	movl	%edx, 64(%rsp)
	movl	%ecx, 68(%rsp)
	jmp	.L193
	.p2align 4,,10
	.p2align 3
.L318:
	movl	$2048, %edx
	addl	%ecx, %ecx
	subl	%r10d, %edx
	shrl	$5, %edx
	addl	%edx, %r9d
	movl	%edi, %edx
	movw	%r9w, (%r11)
	jmp	.L157
	.p2align 4,,10
	.p2align 3
.L245:
	subl	$5, %ecx
	movq	%rbx, %r10
	.p2align 4,,10
	.p2align 3
.L251:
	cmpl	$16777215, %edx
	ja	.L250
	movzbl	(%r10), %edi
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %r10
	orl	%edi, %eax
.L250:
	shrl	%edx
	leal	1(%r9,%r9), %r9d
	subl	%edx, %eax
	movl	%eax, %edi
	sarl	$31, %edi
	addl	%edi, %r9d
	andl	%edx, %edi
	addl	%edi, %eax
	subl	$1, %ecx
	jne	.L251
	movq	(%rsp), %rcx
	movl	%r9d, %edi
	movq	%r10, %rbx
	sall	$4, %edi
	cmpl	$16777215, %edx
	movzwl	1606(%rcx), %r11d
	movl	%r11d, %r9d
	ja	.L252
	movzbl	(%r10), %ecx
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%ecx, %eax
.L252:
	movl	%edx, %ecx
	shrl	$11, %ecx
	imull	%r11d, %ecx
	cmpl	%eax, %ecx
	jbe	.L253
	movl	$2048, %edx
	movq	(%rsp), %r14
	movl	$4, %r10d
	subl	%r11d, %edx
	shrl	$5, %edx
	addl	%r9d, %edx
	movl	$4, %r9d
	movw	%dx, 1606(%r14)
.L254:
	addq	104(%rsp), %r10
	cmpl	$16777215, %ecx
	movzwl	(%r10), %ebp
	movl	%ebp, %r11d
	ja	.L255
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %ecx
	addq	$1, %rbx
	orl	%edx, %eax
.L255:
	movl	%ecx, %edx
	shrl	$11, %edx
	imull	%ebp, %edx
	cmpl	%eax, %edx
	jbe	.L256
	movl	$2048, %ecx
	subl	%ebp, %ecx
	shrl	$5, %ecx
	addl	%r11d, %ecx
	movw	%cx, (%r10)
.L257:
	movq	104(%rsp), %r14
	movl	%r9d, %ecx
	cmpl	$16777215, %edx
	leaq	(%r14,%rcx,2), %rbp
	movzwl	0(%rbp), %r11d
	movl	%r11d, %r10d
	ja	.L258
	movzbl	(%rbx), %ecx
	sall	$8, %eax
	sall	$8, %edx
	addq	$1, %rbx
	orl	%ecx, %eax
.L258:
	movl	%edx, %ecx
	shrl	$11, %ecx
	imull	%r11d, %ecx
	cmpl	%eax, %ecx
	jbe	.L259
	movl	$2048, %edx
	addl	%r9d, %r9d
	subl	%r11d, %edx
	shrl	$5, %edx
	addl	%r10d, %edx
	movw	%dx, 0(%rbp)
.L260:
	movq	104(%rsp), %rdx
	cmpl	$16777215, %ecx
	leaq	(%rdx,%r9,2), %r11
	movzwl	(%r11), %r10d
	movl	%r10d, %r9d
	ja	.L261
	movzbl	(%rbx), %edx
	sall	$8, %eax
	sall	$8, %ecx
	addq	$1, %rbx
	orl	%edx, %eax
.L261:
	movl	%ecx, %edx
	shrl	$11, %edx
	imull	%r10d, %edx
	cmpl	%eax, %edx
	jbe	.L262
	movl	$2048, %ecx
	subl	%r10d, %ecx
	shrl	$5, %ecx
	addl	%r9d, %ecx
	movw	%cx, (%r11)
.L263:
	cmpl	$-1, %edi
	jne	.L244
	leal	274(%r15), %ecx
	subl	$12, %esi
	movl	%ecx, 56(%rsp)
	jmp	.L264
	.p2align 4,,10
	.p2align 3
.L220:
	shrl	$5, %r10d
	subl	%edx, %ecx
	subl	%edx, %eax
	subl	%r10d, %edi
	leal	1(%r9,%r9), %r15d
	movl	%ecx, %edx
	movw	%di, (%r11)
	jmp	.L210
	.p2align 4,,10
	.p2align 3
.L214:
	shrl	$5, %r9d
	subl	%edx, %ecx
	subl	%edx, %eax
	subl	%r9d, %edi
	movl	%ecx, %edx
	movl	$6, %r9d
	movw	%di, 2(%r10)
	movl	$6, %edi
	jmp	.L215
	.p2align 4,,10
	.p2align 3
.L217:
	shrl	$5, %ebp
	subl	%ecx, %edx
	subl	%ecx, %eax
	subl	%ebp, %r11d
	addl	$1, %r9d
	movl	%edx, %ecx
	movw	%r11w, (%rdi)
	jmp	.L218
	.p2align 4,,10
	.p2align 3
.L197:
	shrl	$5, %r9d
	subl	%edi, %edx
	subl	%edi, %eax
	subl	%r9d, %ecx
	movl	%edx, %edi
	movl	12(%rsp), %edx
	movw	%cx, (%r10)
	movl	80(%rsp), %ecx
	movl	%ecx, 12(%rsp)
	movl	68(%rsp), %ecx
	movl	%ecx, 80(%rsp)
	movl	64(%rsp), %ecx
	movl	%edx, 64(%rsp)
	movl	%ecx, 68(%rsp)
	jmp	.L193
	.p2align 4,,10
	.p2align 3
.L126:
	movl	64(%rax), %edx
	movq	112(%rsp), %rcx
	movl	12(%rax), %eax
	subq	%r12, %rcx
	subl	%edx, %eax
	cmpq	%rax, %rcx
	jbe	.L283
	addq	%r12, %rax
	movl	%edx, 8(%rsp)
	movq	%rax, 16(%rsp)
	jmp	.L127
	.p2align 4,,10
	.p2align 3
.L259:
	shrl	$5, %r11d
	subl	%ecx, %edx
	subl	%ecx, %eax
	subl	%r11d, %r10d
	leal	1(%r9,%r9), %r9d
	orl	$4, %edi
	movw	%r10w, 0(%rbp)
	movl	%edx, %ecx
	jmp	.L260
	.p2align 4,,10
	.p2align 3
.L253:
	movq	(%rsp), %r14
	shrl	$5, %r11d
	subl	%ecx, %edx
	subl	%r11d, %r9d
	subl	%ecx, %eax
	orl	$1, %edi
	movl	%edx, %ecx
	movl	$6, %r10d
	movw	%r9w, 1606(%r14)
	movl	$6, %r9d
	jmp	.L254
	.p2align 4,,10
	.p2align 3
.L262:
	shrl	$5, %r10d
	subl	%edx, %ecx
	subl	%edx, %eax
	subl	%r10d, %r9d
	orl	$8, %edi
	movl	%ecx, %edx
	movw	%r9w, (%r11)
	jmp	.L263
	.p2align 4,,10
	.p2align 3
.L256:
	shrl	$5, %ebp
	subl	%edx, %ecx
	subl	%edx, %eax
	subl	%ebp, %r11d
	addl	$1, %r9d
	orl	$2, %edi
	movw	%r11w, (%r10)
	movl	%ecx, %edx
	jmp	.L257
.L283:
	movq	112(%rsp), %rax
	movl	%edx, 8(%rsp)
	movq	%rax, 16(%rsp)
	jmp	.L127
.L280:
	movq	72(%rsp), %rax
	movl	92(%rax), %ecx
	xorl	%eax, %eax
	cmpl	$274, %ecx
	ja	.L321
	jmp	.L299
	.cfi_endproc
.LFE29:
	.size	LzmaDec_DecodeReal2, .-LzmaDec_DecodeReal2
	.section	.text.unlikely
.LCOLDE4:
	.text
.LHOTE4:
	.section	.text.unlikely
.LCOLDB5:
	.text
.LHOTB5:
	.p2align 4,,15
	.globl	do_copy1
	.type	do_copy1, @function
do_copy1:
.LFB24:
	.cfi_startproc
	movl	%ecx, %eax
	addq	%rsi, %rdi
	subq	%rsi, %rdx
	leaq	(%rdi,%rax), %r8
	addq	%rsi, %rax
	.p2align 4,,10
	.p2align 3
.L323:
	movzbl	(%rdi,%rdx), %esi
	addq	$1, %rdi
	movb	%sil, -1(%rdi)
	cmpq	%r8, %rdi
	jne	.L323
	rep ret
	.cfi_endproc
.LFE24:
	.size	do_copy1, .-do_copy1
	.section	.text.unlikely
.LCOLDE5:
	.text
.LHOTE5:
	.section	.text.unlikely
.LCOLDB6:
	.text
.LHOTB6:
	.p2align 4,,15
	.globl	do_copy2
	.type	do_copy2, @function
do_copy2:
.LFB25:
	.cfi_startproc
	leal	-1(%r8), %eax
	leaq	(%rdi,%rsi), %r9
	xorl	%r8d, %r8d
	leaq	1(%rsi,%rax), %rax
	leaq	(%rdi,%rax), %r10
	.p2align 4,,10
	.p2align 3
.L327:
	movzbl	(%rdi,%rdx), %esi
	addq	$1, %rdx
	cmpq	%rcx, %rdx
	cmove	%r8, %rdx
	addq	$1, %r9
	movb	%sil, -1(%r9)
	cmpq	%r10, %r9
	jne	.L327
	rep ret
	.cfi_endproc
.LFE25:
	.size	do_copy2, .-do_copy2
	.section	.text.unlikely
.LCOLDE6:
	.text
.LHOTE6:
	.section	.text.unlikely
.LCOLDB7:
	.text
.LHOTB7:
	.p2align 4,,15
	.globl	LzmaDec_InitDicAndState
	.type	LzmaDec_InitDicAndState, @function
LzmaDec_InitDicAndState:
.LFB31:
	.cfi_startproc
	testl	%esi, %esi
	movl	$1, 96(%rdi)
	movl	$0, 92(%rdi)
	movl	$0, 108(%rdi)
	je	.L330
	movl	$0, 64(%rdi)
	movl	$0, 68(%rdi)
	movl	$1, 100(%rdi)
.L330:
	testl	%edx, %edx
	je	.L329
	movl	$1, 100(%rdi)
.L329:
	rep ret
	.cfi_endproc
.LFE31:
	.size	LzmaDec_InitDicAndState, .-LzmaDec_InitDicAndState
	.section	.text.unlikely
.LCOLDE7:
	.text
.LHOTE7:
	.section	.text.unlikely
.LCOLDB8:
	.text
.LHOTB8:
	.p2align 4,,15
	.globl	LzmaDec_Init
	.type	LzmaDec_Init, @function
LzmaDec_Init:
.LFB32:
	.cfi_startproc
	movq	$0, 48(%rdi)
	movl	$1, 96(%rdi)
	movl	$0, 92(%rdi)
	movl	$0, 108(%rdi)
	movl	$0, 64(%rdi)
	movl	$0, 68(%rdi)
	movl	$1, 100(%rdi)
	ret
	.cfi_endproc
.LFE32:
	.size	LzmaDec_Init, .-LzmaDec_Init
	.section	.text.unlikely
.LCOLDE8:
	.text
.LHOTE8:
	.section	.text.unlikely
.LCOLDB9:
	.text
.LHOTB9:
	.p2align 4,,15
	.globl	LzmaDec_DecodeToDic
	.type	LzmaDec_DecodeToDic, @function
LzmaDec_DecodeToDic:
.LFB34:
	.cfi_startproc
	pushq	%r15
	.cfi_def_cfa_offset 16
	.cfi_offset 15, -16
	pushq	%r14
	.cfi_def_cfa_offset 24
	.cfi_offset 14, -24
	movq	%rdx, %r15
	pushq	%r13
	.cfi_def_cfa_offset 32
	.cfi_offset 13, -32
	pushq	%r12
	.cfi_def_cfa_offset 40
	.cfi_offset 12, -40
	movq	%r9, %r13
	pushq	%rbp
	.cfi_def_cfa_offset 48
	.cfi_offset 6, -48
	pushq	%rbx
	.cfi_def_cfa_offset 56
	.cfi_offset 3, -56
	movq	%rdi, %rbx
	movq	%rsi, %rbp
	movq	%rcx, %r14
	subq	$40, %rsp
	.cfi_def_cfa_offset 96
	movq	(%rcx), %r12
	movq	$0, (%rcx)
	movl	%r8d, 16(%rsp)
	movq	%r9, 24(%rsp)
	call	LzmaDec_WriteRem
	leaq	112(%rbx), %rax
	movl	$0, 0(%r13)
	movq	%rax, (%rsp)
.L340:
	movl	92(%rbx), %esi
	cmpl	$274, %esi
	je	.L413
.L372:
	movl	96(%rbx), %r9d
	testl	%r9d, %r9d
	je	.L341
	testq	%r12, %r12
	je	.L342
	movl	108(%rbx), %eax
	cmpl	$4, %eax
	jbe	.L345
	jmp	.L346
	.p2align 4,,10
	.p2align 3
.L414:
	movl	%edx, %eax
	cmpl	$5, %eax
	je	.L346
.L345:
	leal	1(%rax), %edx
	addq	$1, %r15
	movl	%edx, 108(%rbx)
	movzbl	-1(%r15), %ecx
	movb	%cl, 112(%rbx,%rax)
	addq	$1, (%r14)
	subq	$1, %r12
	jne	.L414
.L344:
	cmpl	$4, %edx
	jbe	.L411
	xorl	%r12d, %r12d
.L346:
	cmpb	$0, 112(%rbx)
	jne	.L412
	movzbl	113(%rbx), %eax
	movl	$-1, 40(%rbx)
	movl	$0, 96(%rbx)
	movl	$0, 108(%rbx)
	sall	$24, %eax
	movl	%eax, %edx
	movzbl	114(%rbx), %eax
	sall	$16, %eax
	orl	%edx, %eax
	movzbl	116(%rbx), %edx
	orl	%edx, %eax
	movzbl	115(%rbx), %edx
	sall	$8, %edx
	orl	%edx, %eax
	movl	%eax, 44(%rbx)
.L341:
	cmpq	48(%rbx), %rbp
	ja	.L377
	testl	%esi, %esi
	jne	.L350
	movl	44(%rbx), %r8d
	testl	%r8d, %r8d
	je	.L415
	movl	16(%rsp), %eax
	testl	%eax, %eax
	je	.L375
	movl	100(%rbx), %edi
	movl	$1, %r9d
	testl	%edi, %edi
	jne	.L416
.L352:
	movl	108(%rbx), %eax
	testl	%eax, %eax
	je	.L355
.L418:
	cmpl	$19, %eax
	ja	.L379
	xorl	%r13d, %r13d
	testq	%r12, %r12
	leaq	1(%r12), %rdi
	movl	$1, %edx
	movl	%eax, %ecx
	jne	.L396
	jmp	.L358
	.p2align 4,,10
	.p2align 3
.L417:
	addq	$1, %rdx
	movl	%ecx, %eax
	cmpq	%rdi, %rdx
	je	.L358
.L396:
	movzbl	-1(%r15,%rdx), %esi
	leal	1(%rax), %ecx
	movl	%edx, %r13d
	cmpl	$20, %ecx
	movb	%sil, 112(%rbx,%rax)
	jne	.L417
.L356:
	testb	%r9b, %r9b
	movl	%ecx, 108(%rbx)
	jne	.L374
.L371:
	movq	(%rsp), %rax
	movq	%rbp, %rsi
	movq	%rbx, %rdi
	movl	%ecx, 12(%rsp)
	movq	%rax, 32(%rbx)
	movq	%rax, %rdx
	call	LzmaDec_DecodeReal2
	testl	%eax, %eax
	jne	.L412
	movq	32(%rbx), %rsi
	subq	(%rsp), %rsi
	movl	12(%rsp), %ecx
	cmpl	%ecx, %esi
	ja	.L381
	subl	%esi, %ecx
	cmpl	%r13d, %ecx
	ja	.L381
	movl	92(%rbx), %esi
	movl	%r13d, %r11d
	subl	%ecx, %r11d
	addq	%r11, (%r14)
	movl	$0, 108(%rbx)
	addq	%r11, %r15
	subq	%r11, %r12
	cmpl	$274, %esi
	jne	.L372
.L413:
	movl	44(%rbx), %eax
	testl	%eax, %eax
	jne	.L373
	movq	24(%rsp), %rdi
	movl	$1, (%rdi)
.L373:
	testl	%eax, %eax
	setne	%al
	addq	$40, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	popq	%rbx
	.cfi_def_cfa_offset 48
	movzbl	%al, %eax
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L377:
	.cfi_restore_state
	movl	100(%rbx), %edi
	xorl	%r9d, %r9d
	testl	%edi, %edi
	je	.L352
.L416:
	movl	(%rbx), %ecx
	addl	4(%rbx), %ecx
	movl	$768, %esi
	xorl	%eax, %eax
	movq	16(%rbx), %rdx
	sall	%cl, %esi
	leal	1846(%rsi), %ecx
	testl	%ecx, %ecx
	je	.L354
	.p2align 4,,10
	.p2align 3
.L397:
	movl	$1024, %esi
	movw	%si, (%rdx,%rax,2)
	addq	$1, %rax
	cmpq	%rax, %rcx
	ja	.L397
.L354:
	movl	108(%rbx), %eax
	movl	$1, 88(%rbx)
	movl	$1, 84(%rbx)
	movl	$1, 80(%rbx)
	movl	$1, 76(%rbx)
	movl	$0, 72(%rbx)
	movl	$0, 100(%rbx)
	testl	%eax, %eax
	jne	.L418
.L355:
	testb	%r9b, %r9b
	jne	.L382
	cmpq	$19, %r12
	leaq	-20(%r15,%r12), %rdx
	setbe	%al
	testb	%al, %al
	jne	.L382
.L364:
	movq	%r15, 32(%rbx)
	movq	%rbp, %rsi
	movq	%rbx, %rdi
	call	LzmaDec_DecodeReal2
	testl	%eax, %eax
	jne	.L412
	movq	32(%rbx), %rax
	movq	%rax, %rdx
	subq	%r15, %rdx
	movq	%rax, %r15
	addq	%rdx, (%r14)
	subq	%rdx, %r12
	jmp	.L340
	.p2align 4,,10
	.p2align 3
.L350:
	movl	16(%rsp), %edx
	testl	%edx, %edx
	je	.L375
.L362:
	movq	24(%rsp), %rax
	movl	$2, (%rax)
.L412:
	movl	$1, %eax
.L409:
	addq	$40, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L358:
	.cfi_restore_state
	movl	%ecx, 108(%rbx)
.L374:
	movq	(%rsp), %rsi
	movl	%ecx, %edx
	movq	%rbx, %rdi
	movb	%r9b, 23(%rsp)
	movl	%ecx, 12(%rsp)
	call	LzmaDec_TryDummy
	testl	%eax, %eax
	movl	12(%rsp), %ecx
	movzbl	23(%rsp), %r9d
	je	.L419
	cmpl	$2, %eax
	je	.L371
	testb	%r9b, %r9b
	je	.L371
	jmp	.L362
	.p2align 4,,10
	.p2align 3
.L382:
	movq	%r12, %rdx
	movq	%r15, %rsi
	movq	%rbx, %rdi
	movb	%r9b, 12(%rsp)
	call	LzmaDec_TryDummy
	testl	%eax, %eax
	movzbl	12(%rsp), %r9d
	je	.L420
	cmpl	$2, %eax
	je	.L383
	testb	%r9b, %r9b
	jne	.L362
.L383:
	movq	%r15, %rdx
	jmp	.L364
.L342:
	movl	108(%rbx), %edx
	jmp	.L344
.L379:
	movl	%eax, %ecx
	xorl	%r13d, %r13d
	jmp	.L356
.L381:
	addq	$40, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	movl	$11, %eax
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
.L420:
	.cfi_restore_state
	leaq	112(%rbx), %rdi
	movq	%r12, %rdx
	movq	%r15, %rsi
	call	memcpy
	movl	%r12d, 108(%rbx)
	addq	%r12, (%r14)
.L411:
	movq	24(%rsp), %rax
	movl	$3, (%rax)
	addq	$40, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	xorl	%eax, %eax
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
.L375:
	.cfi_restore_state
	movq	24(%rsp), %rax
	movl	$2, (%rax)
	xorl	%eax, %eax
	jmp	.L409
.L415:
	movq	24(%rsp), %rax
	movl	$4, (%rax)
	xorl	%eax, %eax
	jmp	.L409
.L419:
	movq	24(%rsp), %rax
	movl	%r13d, %r11d
	addq	%r11, (%r14)
	movl	$3, (%rax)
	xorl	%eax, %eax
	jmp	.L409
	.cfi_endproc
.LFE34:
	.size	LzmaDec_DecodeToDic, .-LzmaDec_DecodeToDic
	.section	.text.unlikely
.LCOLDE9:
	.text
.LHOTE9:
	.section	.text.unlikely
.LCOLDB10:
	.text
.LHOTB10:
	.p2align 4,,15
	.globl	LzmaDec_DecodeToBuf
	.type	LzmaDec_DecodeToBuf, @function
LzmaDec_DecodeToBuf:
.LFB35:
	.cfi_startproc
	pushq	%r15
	.cfi_def_cfa_offset 16
	.cfi_offset 15, -16
	pushq	%r14
	.cfi_def_cfa_offset 24
	.cfi_offset 14, -24
	movq	%rdx, %r11
	pushq	%r13
	.cfi_def_cfa_offset 32
	.cfi_offset 13, -32
	pushq	%r12
	.cfi_def_cfa_offset 40
	.cfi_offset 12, -40
	movq	%rsi, %r14
	pushq	%rbp
	.cfi_def_cfa_offset 48
	.cfi_offset 6, -48
	pushq	%rbx
	.cfi_def_cfa_offset 56
	.cfi_offset 3, -56
	movq	%rdi, %rbp
	movq	%rcx, %r13
	movq	%r8, %r15
	subq	$72, %rsp
	.cfi_def_cfa_offset 128
	movq	(%rdx), %r10
	movq	(%r8), %r12
	movq	128(%rsp), %rax
	movq	$0, (%rdx)
	movl	%r9d, 44(%rsp)
	movq	$0, (%r8)
	movq	%rax, 32(%rsp)
	movq	%fs:40, %rax
	movq	%rax, 56(%rsp)
	xorl	%eax, %eax
	jmp	.L425
	.p2align 4,,10
	.p2align 3
.L422:
	movq	%rsi, %rax
	xorl	%r8d, %r8d
	subq	%rbx, %rax
	cmpq	%rax, %r10
	ja	.L423
	movl	44(%rsp), %r8d
	leaq	(%rbx,%r10), %rsi
.L423:
	movq	32(%rsp), %r9
	leaq	48(%rsp), %rcx
	movq	%r13, %rdx
	movq	%rbp, %rdi
	movq	%r11, 24(%rsp)
	movq	%r10, 16(%rsp)
	call	LzmaDec_DecodeToDic
	movl	%eax, 40(%rsp)
	movq	48(%rsp), %rax
	movq	%r14, %rdi
	addq	%rax, (%r15)
	movq	48(%rbp), %rcx
	addq	%rax, %r13
	subq	%rax, %r12
	subq	%rbx, %rcx
	addq	24(%rbp), %rbx
	movq	%rcx, %rdx
	movq	%rcx, 8(%rsp)
	movq	%rbx, %rsi
	call	memcpy
	movq	8(%rsp), %rcx
	movq	16(%rsp), %r10
	movq	24(%rsp), %r11
	movl	40(%rsp), %r8d
	addq	%rcx, %r14
	subq	%rcx, %r10
	addq	%rcx, (%r11)
	testl	%r8d, %r8d
	jne	.L428
	testq	%r10, %r10
	je	.L429
	testq	%rcx, %rcx
	je	.L429
.L425:
	movq	48(%rbp), %rbx
	movq	56(%rbp), %rsi
	movq	%r12, 48(%rsp)
	cmpq	%rsi, %rbx
	jne	.L422
	movq	$0, 48(%rbp)
	xorl	%ebx, %ebx
	jmp	.L422
	.p2align 4,,10
	.p2align 3
.L428:
	movl	%r8d, %eax
.L424:
	movq	56(%rsp), %rdx
	xorq	%fs:40, %rdx
	jne	.L431
	addq	$72, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L429:
	.cfi_restore_state
	xorl	%eax, %eax
	jmp	.L424
.L431:
	call	__stack_chk_fail
	.cfi_endproc
.LFE35:
	.size	LzmaDec_DecodeToBuf, .-LzmaDec_DecodeToBuf
	.section	.text.unlikely
.LCOLDE10:
	.text
.LHOTE10:
	.section	.text.unlikely
.LCOLDB11:
	.text
.LHOTB11:
	.p2align 4,,15
	.globl	LzmaDec_FreeProbs
	.type	LzmaDec_FreeProbs, @function
LzmaDec_FreeProbs:
.LFB36:
	.cfi_startproc
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset 3, -16
	movq	%rsi, %rax
	movq	%rdi, %rbx
	movq	16(%rdi), %rsi
	movq	%rax, %rdi
	call	*8(%rax)
	movq	$0, 16(%rbx)
	popq	%rbx
	.cfi_def_cfa_offset 8
	ret
	.cfi_endproc
.LFE36:
	.size	LzmaDec_FreeProbs, .-LzmaDec_FreeProbs
	.section	.text.unlikely
.LCOLDE11:
	.text
.LHOTE11:
	.section	.text.unlikely
.LCOLDB12:
	.text
.LHOTB12:
	.p2align 4,,15
	.globl	LzmaDec_Free
	.type	LzmaDec_Free, @function
LzmaDec_Free:
.LFB38:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	pushq	%rbx
	.cfi_def_cfa_offset 24
	.cfi_offset 3, -24
	movq	%rsi, %rbp
	movq	%rdi, %rbx
	subq	$8, %rsp
	.cfi_def_cfa_offset 32
	movq	16(%rdi), %rsi
	movq	%rbp, %rdi
	call	*8(%rbp)
	movq	$0, 16(%rbx)
	movq	24(%rbx), %rsi
	movq	%rbp, %rdi
	call	*8(%rbp)
	movq	$0, 24(%rbx)
	addq	$8, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%rbp
	.cfi_def_cfa_offset 8
	ret
	.cfi_endproc
.LFE38:
	.size	LzmaDec_Free, .-LzmaDec_Free
	.section	.text.unlikely
.LCOLDE12:
	.text
.LHOTE12:
	.section	.text.unlikely
.LCOLDB13:
	.text
.LHOTB13:
	.p2align 4,,15
	.globl	LzmaProps_Decode
	.type	LzmaProps_Decode, @function
LzmaProps_Decode:
.LFB39:
	.cfi_startproc
	cmpl	$4, %edx
	ja	.L438
	movl	$4, %eax
	ret
	.p2align 4,,10
	.p2align 3
.L438:
	jmp	LzmaProps_Decode.part.2
	.cfi_endproc
.LFE39:
	.size	LzmaProps_Decode, .-LzmaProps_Decode
	.section	.text.unlikely
.LCOLDE13:
	.text
.LHOTE13:
	.section	.text.unlikely
.LCOLDB14:
	.text
.LHOTB14:
	.p2align 4,,15
	.globl	LzmaDec_AllocateProbs
	.type	LzmaDec_AllocateProbs, @function
LzmaDec_AllocateProbs:
.LFB41:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	pushq	%rbx
	.cfi_def_cfa_offset 24
	.cfi_offset 3, -24
	movl	$4, %ebx
	subq	$56, %rsp
	.cfi_def_cfa_offset 80
	movq	%fs:40, %rax
	movq	%rax, 40(%rsp)
	xorl	%eax, %eax
	cmpl	$4, %edx
	ja	.L445
.L440:
	movq	40(%rsp), %rdi
	xorq	%fs:40, %rdi
	movl	%ebx, %eax
	jne	.L446
	addq	$56, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%rbp
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L445:
	.cfi_restore_state
	movq	%rdi, %rbp
	leaq	16(%rsp), %rdi
	movq	%rcx, 8(%rsp)
	call	LzmaProps_Decode.part.2
	testl	%eax, %eax
	movl	%eax, %ebx
	jne	.L440
	movq	8(%rsp), %rcx
	movl	16(%rsp), %edx
	leaq	104(%rbp), %rsi
	leaq	16(%rbp), %rdi
	movq	%rcx, %r8
	movl	20(%rsp), %ecx
	call	LzmaDec_AllocateProbs2.isra.1
	testl	%eax, %eax
	jne	.L443
	movq	16(%rsp), %rax
	movq	24(%rsp), %rdx
	movq	%rax, 0(%rbp)
	movq	%rdx, 8(%rbp)
	jmp	.L440
	.p2align 4,,10
	.p2align 3
.L443:
	movl	%eax, %ebx
	jmp	.L440
.L446:
	call	__stack_chk_fail
	.cfi_endproc
.LFE41:
	.size	LzmaDec_AllocateProbs, .-LzmaDec_AllocateProbs
	.section	.text.unlikely
.LCOLDE14:
	.text
.LHOTE14:
	.section	.text.unlikely
.LCOLDB15:
	.text
.LHOTB15:
	.p2align 4,,15
	.globl	LzmaDec_Allocate
	.type	LzmaDec_Allocate, @function
LzmaDec_Allocate:
.LFB42:
	.cfi_startproc
	pushq	%r13
	.cfi_def_cfa_offset 16
	.cfi_offset 13, -16
	pushq	%r12
	.cfi_def_cfa_offset 24
	.cfi_offset 12, -24
	pushq	%rbp
	.cfi_def_cfa_offset 32
	.cfi_offset 6, -32
	pushq	%rbx
	.cfi_def_cfa_offset 40
	.cfi_offset 3, -40
	movl	$4, %ebp
	subq	$40, %rsp
	.cfi_def_cfa_offset 80
	movq	%fs:40, %rax
	movq	%rax, 24(%rsp)
	xorl	%eax, %eax
	cmpl	$4, %edx
	ja	.L461
.L448:
	movq	24(%rsp), %rcx
	xorq	%fs:40, %rcx
	movl	%ebp, %eax
	jne	.L462
	addq	$40, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 40
	popq	%rbx
	.cfi_def_cfa_offset 32
	popq	%rbp
	.cfi_def_cfa_offset 24
	popq	%r12
	.cfi_def_cfa_offset 16
	popq	%r13
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L461:
	.cfi_restore_state
	movq	%rdi, %rbx
	movq	%rsp, %rdi
	movq	%rcx, %r13
	call	LzmaProps_Decode.part.2
	testl	%eax, %eax
	movl	%eax, %ebp
	jne	.L448
	movl	4(%rsp), %ecx
	movl	(%rsp), %edx
	leaq	104(%rbx), %rsi
	leaq	16(%rbx), %rdi
	movq	%r13, %r8
	call	LzmaDec_AllocateProbs2.isra.1
	testl	%eax, %eax
	jne	.L454
	movl	12(%rsp), %edx
	movq	$-4194304, %rax
	movl	$4194303, %r12d
	cmpl	$1073741823, %edx
	jbe	.L463
.L449:
	addq	%rdx, %r12
	movq	24(%rbx), %rsi
	andq	%r12, %rax
	cmpq	%rdx, %rax
	cmovnb	%rax, %rdx
	testq	%rsi, %rsi
	movq	%rdx, %r12
	je	.L450
	cmpq	%rdx, 56(%rbx)
	je	.L451
.L450:
	movq	%r13, %rdi
	call	*8(%r13)
	movq	$0, 24(%rbx)
	movq	%r12, %rsi
	movq	%r13, %rdi
	call	*0(%r13)
	testq	%rax, %rax
	movq	%rax, 24(%rbx)
	je	.L464
.L451:
	movq	(%rsp), %rax
	movq	8(%rsp), %rdx
	movq	%r12, 56(%rbx)
	movq	%rax, (%rbx)
	movq	%rdx, 8(%rbx)
	jmp	.L448
	.p2align 4,,10
	.p2align 3
.L463:
	cmpl	$4194304, %edx
	sbbq	%rax, %rax
	andl	$1044480, %eax
	subq	$1048576, %rax
	cmpl	$4194304, %edx
	sbbq	%r12, %r12
	andq	$-1044480, %r12
	addq	$1048575, %r12
	jmp	.L449
	.p2align 4,,10
	.p2align 3
.L454:
	movl	%eax, %ebp
	jmp	.L448
	.p2align 4,,10
	.p2align 3
.L464:
	movq	16(%rbx), %rsi
	movq	%r13, %rdi
	movl	$2, %ebp
	call	*8(%r13)
	movq	$0, 16(%rbx)
	jmp	.L448
.L462:
	call	__stack_chk_fail
	.cfi_endproc
.LFE42:
	.size	LzmaDec_Allocate, .-LzmaDec_Allocate
	.section	.text.unlikely
.LCOLDE15:
	.text
.LHOTE15:
	.section	.text.unlikely
.LCOLDB16:
	.text
.LHOTB16:
	.p2align 4,,15
	.globl	LzmaDecode
	.type	LzmaDecode, @function
LzmaDecode:
.LFB43:
	.cfi_startproc
	pushq	%r15
	.cfi_def_cfa_offset 16
	.cfi_offset 15, -16
	pushq	%r14
	.cfi_def_cfa_offset 24
	.cfi_offset 14, -24
	pushq	%r13
	.cfi_def_cfa_offset 32
	.cfi_offset 13, -32
	pushq	%r12
	.cfi_def_cfa_offset 40
	.cfi_offset 12, -40
	pushq	%rbp
	.cfi_def_cfa_offset 48
	.cfi_offset 6, -48
	pushq	%rbx
	.cfi_def_cfa_offset 56
	.cfi_offset 3, -56
	movq	%rsi, %rbp
	movq	%rcx, %rbx
	subq	$168, %rsp
	.cfi_def_cfa_offset 224
	movq	(%rcx), %r11
	movq	(%rsi), %r14
	movq	232(%rsp), %r12
	movq	$0, (%rcx)
	movq	%fs:40, %rax
	movq	%rax, 152(%rsp)
	xorl	%eax, %eax
	cmpq	$4, %r11
	movq	$0, (%rsi)
	movq	240(%rsp), %r13
	movl	$0, (%r12)
	movb	$6, %al
	ja	.L472
.L466:
	movq	152(%rsp), %rdx
	xorq	%fs:40, %rdx
	jne	.L473
	addq	$168, %rsp
	.cfi_remember_state
	.cfi_def_cfa_offset 56
	popq	%rbx
	.cfi_def_cfa_offset 48
	popq	%rbp
	.cfi_def_cfa_offset 40
	popq	%r12
	.cfi_def_cfa_offset 32
	popq	%r13
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	ret
	.p2align 4,,10
	.p2align 3
.L472:
	.cfi_restore_state
	movq	%rdi, 8(%rsp)
	leaq	16(%rsp), %rdi
	movq	%rdx, %r15
	movq	%r13, %rcx
	movl	%r9d, %edx
	movq	%r8, %rsi
	movq	%r11, (%rsp)
	movq	$0, 40(%rsp)
	movq	$0, 32(%rsp)
	call	LzmaDec_AllocateProbs
	testl	%eax, %eax
	movq	(%rsp), %r11
	movq	8(%rsp), %r10
	jne	.L466
	movl	224(%rsp), %r8d
	leaq	16(%rsp), %rdi
	movq	%r11, (%rbx)
	movq	%rbx, %rcx
	movq	%r12, %r9
	movq	%r15, %rdx
	movq	%r14, %rsi
	movq	%r10, 40(%rsp)
	movq	%r14, 72(%rsp)
	movq	$0, 64(%rsp)
	movl	$1, 112(%rsp)
	movl	$0, 108(%rsp)
	movl	$0, 124(%rsp)
	movl	$0, 80(%rsp)
	movl	$0, 84(%rsp)
	movl	$1, 116(%rsp)
	call	LzmaDec_DecodeToDic
	movl	%eax, %ebx
	movq	64(%rsp), %rax
	testl	%ebx, %ebx
	movq	%rax, 0(%rbp)
	jne	.L467
	cmpl	$3, (%r12)
	movl	$6, %eax
	cmove	%eax, %ebx
.L467:
	movq	32(%rsp), %rsi
	movq	%r13, %rdi
	call	*8(%r13)
	movl	%ebx, %eax
	jmp	.L466
.L473:
	call	__stack_chk_fail
	.cfi_endproc
.LFE43:
	.size	LzmaDecode, .-LzmaDecode
	.section	.text.unlikely
.LCOLDE16:
	.text
.LHOTE16:
	.ident	"GCC: (Ubuntu 4.9.3-1ubuntu1~ubuntu15.04.1~ppa1) 4.9.3"
	.section	.note.GNU-stack,"",@progbits
