import React from 'react';
import './templateStyle.css';

const Template = (props) => <div className={'template'}>
    <span className={'name'}>{props.name}</span>
    <span className={'description'}>{props.description}</span>
    <span className={'id'}>{props.id}</span>
</div>;

export default Template;
