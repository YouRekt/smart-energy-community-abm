import { Button } from '@/components/ui/button';
import { ButtonGroup } from '@/components/ui/button-group';
import { Input } from '@/components/ui/input';
import { Minus, Plus } from 'lucide-react';
import { useCallback, useRef } from 'react';

interface HTMLInputWithTracker extends HTMLInputElement {
	_valueTracker?: {
		setValue: (value: string | undefined) => void;
	};
}

function useSpinButton(action: () => void) {
	const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
	const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

	const stop = useCallback(() => {
		if (intervalRef.current) clearInterval(intervalRef.current);
		if (timeoutRef.current) clearTimeout(timeoutRef.current);
	}, []);

	const start = useCallback(() => {
		action();
		timeoutRef.current = setTimeout(() => {
			intervalRef.current = setInterval(() => {
				action();
			}, 50);
		}, 500);
	}, [action]);
	return {
		onMouseDown: start,
		onMouseUp: stop,
		onMouseLeave: stop,
		onTouchStart: start,
		onTouchEnd: stop,
	};
}

function NumberInput({
	value,
	min,
	max,
	step,
	...props
}: Omit<React.ComponentProps<'input'>, 'type'>) {
	const inputRef = useRef<HTMLInputElement>(null);

	const triggerChange = useCallback(() => {
		if (inputRef.current) {
			const nativeEvent = new Event('change', { bubbles: true });

			const tracker = (inputRef.current as HTMLInputWithTracker)
				._valueTracker;
			if (tracker) {
				tracker.setValue(undefined);
			}

			inputRef.current.dispatchEvent(nativeEvent);
		}
	}, []);

	const decrement = useCallback(() => {
		if (inputRef.current) {
			inputRef.current.stepDown();
			triggerChange();
		}
	}, [triggerChange]);

	const increment = useCallback(() => {
		if (inputRef.current) {
			inputRef.current.stepUp();
			triggerChange();
		}
	}, [triggerChange]);

	const decreaseHandlers = useSpinButton(decrement);
	const increaseHandlers = useSpinButton(increment);

	return (
		<ButtonGroup>
			<Input
				type='number'
				value={value}
				step={step}
				min={min}
				max={max}
				ref={inputRef}
				{...props}
			/>

			<Button
				variant='outline'
				type='button'
				aria-label='Decrement'
				disabled={Number(value) - Number(step) < Number(min)}
				{...decreaseHandlers}>
				<Minus className='h-4 w-4' />
			</Button>

			<Button
				variant='outline'
				type='button'
				aria-label='Increment'
				disabled={Number(value) + Number(step) > Number(max)}
				{...increaseHandlers}>
				<Plus className='h-4 w-4' />
			</Button>
		</ButtonGroup>
	);
}

export { NumberInput };
